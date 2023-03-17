package ru.practicum.stats.server.controller;

import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.ViewStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit saveEndpointHit(@Valid @RequestBody CreateEndpointHitDto createEndpointHitDto) {
        return statsService.saveEndpointHit(createEndpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStatistics(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique

    ) {
        if (uris.isEmpty()) {
            return Collections.emptyList();
        }

        return statsService.getStatistics(start, end, uris, unique);
    }
}
