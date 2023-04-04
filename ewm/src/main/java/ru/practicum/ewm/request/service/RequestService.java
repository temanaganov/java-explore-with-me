package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    public List<RequestDto> getAllRequests(long userId) {
        checkUser(userId);

        return requestRepository
                .findAllByRequesterId(userId)
                .stream()
                .map(requestMapper::requestToRequestDto).collect(Collectors.toList());
    }

    @Transactional
    public RequestDto createRequest(long userId, long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        requestRepository.findByRequesterIdAndEventId(userId, eventId).ifPresent(request -> {
            throw new ConflictException();
        });

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException();
        }

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException();
        }

        long eventConfirmedRequests = requestRepository.findCountOfEventConfirmedRequests(eventId);

        if (event.getParticipantLimit() != 0 && eventConfirmedRequests == event.getParticipantLimit()) {
            throw new ConflictException();
        }

        RequestStatus status = RequestStatus.PENDING;

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        Request request = Request
                .builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(status)
                .build();

        return requestMapper.requestToRequestDto(requestRepository.save(request));
    }

    @Transactional
    public RequestDto cancelRequest(long userId, long requestId) {
        checkUser(userId);
        Request request = checkRequest(requestId);

        if (request.getRequester().getId() != userId) {
            throw new NotFoundException("request", requestId);
        }

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.requestToRequestDto(request);
    }

    private User checkUser(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user", userId));
    }

    private Event checkEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("event", eventId));
    }

    private Request checkRequest(long requestId) {
        return requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("request", requestId));
    }
}
