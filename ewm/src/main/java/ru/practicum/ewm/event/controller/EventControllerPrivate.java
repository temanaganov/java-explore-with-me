package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.core.pagination.PaginationMapper;
import ru.practicum.ewm.event.dto.CreateEventDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.event.dto.UpdateEventUserDto;
import ru.practicum.ewm.event.service.EventServicePrivate;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventControllerPrivate {
    private final EventServicePrivate eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(
            @PathVariable long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getAllEvents(userId, PaginationMapper.toPageable(from, size));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable long userId, @Valid @RequestBody CreateEventDto createEventDto) {
        return eventService.createEvent(userId, createEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody UpdateEventUserDto updateEventUserDto
    ) {
        return eventService.updateEvent(userId, eventId, updateEventUserDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateEventRequests(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequestDto updateRequestDto
    ) {
        return eventService.updateEventRequests(userId, eventId, updateRequestDto);
    }
}
