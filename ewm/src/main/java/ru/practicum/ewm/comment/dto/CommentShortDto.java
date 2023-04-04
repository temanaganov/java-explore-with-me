package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentShortDto {
    private Long id;
    private UserShortDto user;
    private String text;
}
