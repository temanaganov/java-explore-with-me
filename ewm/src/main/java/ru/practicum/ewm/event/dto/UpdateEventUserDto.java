package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.EventStateUserAction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserDto extends UpdateEventDto {
    private EventStateUserAction stateAction;
}
