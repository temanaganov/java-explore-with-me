package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.stats.server.model.EndpointHit;

public interface StatsRepository extends JpaRepository<EndpointHit, Long>, QuerydslPredicateExecutor<EndpointHit>, StatsRepositoryCustom {
}
