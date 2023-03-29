package ru.practicum.ewm.event.validation;

import ru.practicum.ewm.core.exception.ConflictException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class MinHoursLaterValidator implements ConstraintValidator<MinHoursLater, LocalDateTime> {
    private int hours;

    @Override
    public void initialize(MinHoursLater minHoursLater) {
        this.hours = minHoursLater.value();
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext constraintValidatorContext) {
        if (eventDate == null) {
            return true;
        }

        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new ConflictException();
        }

        return true;
    }
}
