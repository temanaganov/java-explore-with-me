package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateCommentDto {
    @NotBlank
    @Size(max = 2000)
    private String text;
}
