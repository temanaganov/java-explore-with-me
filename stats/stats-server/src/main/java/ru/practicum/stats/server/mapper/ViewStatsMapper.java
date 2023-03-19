package ru.practicum.stats.server.mapper;

import com.querydsl.core.Tuple;
import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.ViewStats;

@Component
public class ViewStatsMapper {
    public ViewStats tupleToViewStats(Tuple tuple) {
        return new ViewStats(tuple.get(0, String.class), tuple.get(1, String.class), tuple.get(2, Long.class));
    }
}
