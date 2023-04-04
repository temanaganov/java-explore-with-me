package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.EventStateAdminAction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminDto extends UpdateEventDto {
    private EventStateAdminAction stateAction;
}
