package ru.practicum.ewm.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private String message;
    private String reason;
    private HttpStatus status;
    private LocalDateTime timestamp;
}
