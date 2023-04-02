package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompilationDto {
    @NotBlank
    @Size(max = 120)
    private String title;

    private List<Long> events;

    private boolean pinned;
}
