package ru.practicum.stats.server.repository;

import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepositoryCustom {
    List<ViewStats> getStatisticsByUris(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
