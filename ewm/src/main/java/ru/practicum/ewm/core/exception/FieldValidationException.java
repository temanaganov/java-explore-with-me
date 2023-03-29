package ru.practicum.ewm.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldValidationException extends RuntimeException {
    private final String field;
    private final String description;
}
