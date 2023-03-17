package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.stats.dto.ViewStats(e.app, e.uri, count(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE (e.timestamp BETWEEN :start AND :end) AND " +
            "(e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY count(e.ip) DESC")
    List<ViewStats> getStatisticsByUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT new ru.practicum.stats.dto.ViewStats(e.app, e.uri, count(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE (e.timestamp BETWEEN :start AND :end) AND " +
            "(e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY count(DISTINCT e.ip) DESC")
    List<ViewStats> getUniqueStatisticsByUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
