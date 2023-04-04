package ru.practicum.ewm.core.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, long id) {
        super(entity + " with id=" + id + " not found");
    }
}
