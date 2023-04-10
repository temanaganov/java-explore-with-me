package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateCommentDto {
    @NotNull
    private Long eventId;

    @NotBlank
    @Size(max = 2000)
    private String text;
}
