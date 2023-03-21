package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

@Service
public class StatsClient {
    private final WebClient webClient;

    public StatsClient(@Value("${ewm-stats-server.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void saveEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        webClient.post()
                .uri("/hit")
                .body(Mono.just(createEndpointHitDto), CreateEndpointHitDto.class)
                .retrieve();
    }

    public Mono<List<ViewStats>> getStatistics() {
        return webClient.get()
                .uri("/stats")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {});
    }
}
