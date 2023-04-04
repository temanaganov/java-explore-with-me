package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventServiceAdmin;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class EventControllerAdmin {
    private final EventServiceAdmin eventService;

    @GetMapping
    public List<EventDto> getAllEvents(
            @RequestParam(name = "users", required = false) List<Long> userIds,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(name = "categories", required = false) List<Long> categoryIds,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getAllEvents(
                userIds,
                states,
                categoryIds,
                rangeStart,
                rangeEnd,
                from,
                size
        );
    }

    @PatchMapping("/{eventId}")
    public EventDto moderateEvent(@PathVariable long eventId, @Valid @RequestBody UpdateEventAdminDto updateEventDto) {
        return eventService.moderateEvent(eventId, updateEventDto);
    }
}
