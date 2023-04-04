package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private String text;
}
