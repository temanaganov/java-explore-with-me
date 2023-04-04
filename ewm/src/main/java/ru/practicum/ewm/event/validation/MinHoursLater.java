package ru.practicum.ewm.event.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinHoursLaterValidator.class)
public @interface MinHoursLater {
    int value();

    String message() default "EventDate cannot be earlier than two hours from now";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
