package ru.practicum.stats.server.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

@Component
public class EndpointHitMapper {
    public EndpointHit createEndpointHitDtoToEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        return EndpointHit.builder()
                .app(createEndpointHitDto.getApp())
                .uri(createEndpointHitDto.getUri())
                .ip(createEndpointHitDto.getIp())
                .timestamp(createEndpointHitDto.getTimestamp())
                .build();
    }

    public EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}
