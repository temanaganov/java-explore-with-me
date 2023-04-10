package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.event.dto.UpdateEventUserDto;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.dto.CreateEventDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.EventStateUserAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventUtils;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServicePrivate {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    public List<EventShortDto> getAllEvents(long userId, Pageable pageable) {
        List<EventDto> eventDtos = eventRepository
                .findAllByInitiatorId(userId, pageable)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());

        EventUtils.addViewsAndConfirmedRequestsToEvents(eventDtos, statsClient, requestRepository);

        return eventDtos
                .stream()
                .map(eventMapper::eventDtoToEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(long userId, long eventId) {
        checkUser(userId);
        Event event = checkEvent(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("event", eventId);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        EventUtils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto), statsClient, requestRepository);

        return eventDto;
    }

    @Transactional
    public EventDto createEvent(long userId, CreateEventDto createEventDto) {
        User user = checkUser(userId);
        Category category = checkCategory(createEventDto.getCategory());

        Event event = eventMapper.createEventDtoToEvent(createEventDto);

        event.setInitiator(user);
        event.setCategory(category);
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());

        return eventMapper.eventToEventDto(eventRepository.save(event));
    }

    @Transactional
    public EventDto updateEvent(long userId, long eventId, UpdateEventUserDto updateEventUserDto) {
        checkUser(userId);
        Event event = checkEvent(eventId);

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("event", eventId);
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException();
        }

        eventMapper.updateEvent(event, updateEventUserDto);

        if (updateEventUserDto.getCategory() != null) {
            Category category = checkCategory(updateEventUserDto.getCategory());
            event.setCategory(category);
        }

        if (updateEventUserDto.getStateAction() != null) {
            EventState newState = updateEventUserDto.getStateAction() == EventStateUserAction.SEND_TO_REVIEW
                    ? EventState.PENDING
                    : EventState.CANCELED;
            event.setState(newState);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        EventUtils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto), statsClient, requestRepository);

        return eventDto;
    }

    public List<RequestDto> getEventRequests(long userId, long eventId) {
        checkUser(userId);
        checkEvent(eventId);

        return requestRepository
                .findAllByEventIdAndEventInitiatorId(eventId, userId)
                .stream()
                .map(requestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResultDto updateEventRequests(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequestDto updateRequestDto
    ) {
        checkUser(userId);
        Event event = checkEvent(eventId);
        long eventConfirmedRequests = requestRepository.findCountOfEventConfirmedRequests(eventId);

        if (event.getParticipantLimit() != 0 && eventConfirmedRequests == event.getParticipantLimit()) {
            throw new ConflictException();
        }

        List<Request> requests = requestRepository.findRequestsForUpdating(
                eventId,
                userId,
                updateRequestDto.getRequestIds()
        );

        if (updateRequestDto.getStatus() == RequestStatus.REJECTED) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));

            return new EventRequestStatusUpdateResultDto(
                    Collections.emptyList(),
                    requests
                            .stream()
                            .map(requestMapper::requestToRequestDto)
                            .collect(Collectors.toList())
            );
        }

        EventRequestStatusUpdateResultDto response = new EventRequestStatusUpdateResultDto(Collections.emptyList(), Collections.emptyList());

        requests.forEach(request -> {
            if (eventConfirmedRequests < event.getParticipantLimit()) {
                request.setStatus(RequestStatus.CONFIRMED);
                List<RequestDto> newRequests = new ArrayList<>(response.getConfirmedRequests());
                newRequests.add(requestMapper.requestToRequestDto(request));
                response.setConfirmedRequests(newRequests);
            } else {
                request.setStatus(RequestStatus.REJECTED);

                List<RequestDto> newRequests = new ArrayList<>(response.getRejectedRequests());
                newRequests.add(requestMapper.requestToRequestDto(request));
                response.setRejectedRequests(newRequests);
            }
        });

        return response;
    }

    private Event checkEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("event", eventId));
    }

    private User checkUser(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user", userId));
    }

    private Category checkCategory(long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", categoryId));
    }
}
