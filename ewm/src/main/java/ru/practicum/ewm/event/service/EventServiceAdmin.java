package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.EventStateAdminAction;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.utils.EventUtils;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceAdmin {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    public List<EventDto> getAllEvents(
            List<Long> userIds,
            List<EventState> states,
            List<Long> categoryIds,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        List<EventDto> eventDtos = eventRepository
                .findAllByAdminFilters(userIds, states, categoryIds, rangeStart, rangeEnd, from, size)
                .stream()
                .map(eventMapper::eventToEventDto)
                .collect(Collectors.toList());

        EventUtils.addViewsAndConfirmedRequestsToEvents(eventDtos, statsClient, requestRepository);

        return eventDtos;
    }

    @Transactional
    public EventDto moderateEvent(long eventId, UpdateEventAdminDto updateEventDto) {
        Event event = checkEvent(eventId);

        eventMapper.updateEvent(event, updateEventDto);

        if (updateEventDto.getCategory() != null) {
            Category category = checkCategory(updateEventDto.getCategory());
            event.setCategory(category);
        }

        if (updateEventDto.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException();
            }

            EventState newState = updateEventDto.getStateAction() == EventStateAdminAction.PUBLISH_EVENT
                    ? EventState.PUBLISHED
                    : EventState.CANCELED;

            event.setState(newState);
        }

        EventDto eventDto = eventMapper.eventToEventDto(event);

        EventUtils.addViewsAndConfirmedRequestsToEvents(List.of(eventDto), statsClient, requestRepository);

        return eventDto;
    }

    private Event checkEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("event", eventId));
    }

    private Category checkCategory(long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", categoryId));
    }
}
