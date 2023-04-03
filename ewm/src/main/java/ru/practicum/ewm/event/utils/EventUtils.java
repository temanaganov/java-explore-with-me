package ru.practicum.ewm.event.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void addViewsToEvents(List<EventDto> events, StatsClient statsClient) {
        Map<String, EventDto> eventsMap = events
                .stream()
                .collect(Collectors.toMap(event -> "/events/" + event.getId(), event -> event));

        Object rawStatistics = statsClient.getStatistics(
                LocalDateTime.parse("2000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                LocalDateTime.parse("5000-01-01 00:00:00", FORMATTER).format(FORMATTER),
                new ArrayList<>(eventsMap.keySet()),
                false
        ).getBody();

        List<ViewStats> statistics = new ObjectMapper().convertValue(rawStatistics, new TypeReference<>() {});

        statistics.forEach(statistic -> {
            if (eventsMap.containsKey(statistic.getUri())) {
                eventsMap.get(statistic.getUri()).setViews(statistic.getHits());
            }
        });
    }
}
