package ru.practicum.ewm.core.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, long id) {
        super(String.format("%s with id = %d not found", entity, id));
    }
}
