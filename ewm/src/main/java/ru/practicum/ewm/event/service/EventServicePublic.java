package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.CreateEndpointHitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServicePublic {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    public List<EventShortDto> getAllEvents(
            String text,
            List<Long> categoryIds,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            int from,
            int size,
            HttpServletRequest request
    ) {
        sendStatistics(request);
        return eventRepository
                .findAllByPublicFilters(text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size)
                .stream()
                .map(eventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
    }

    public EventDto getEventById(long eventId, HttpServletRequest request) {
        sendStatistics(request);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("event", eventId));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("event", eventId);
        }

        return eventMapper.eventToEventDto(event);
    }

    private void sendStatistics(HttpServletRequest request) {
        statsClient.saveEndpointHit(new CreateEndpointHitDto(
                "ewm",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        ));
    }
}
