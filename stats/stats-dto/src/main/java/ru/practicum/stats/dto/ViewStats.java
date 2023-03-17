package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
