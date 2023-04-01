package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, EventRepositoryCustom {
    List<Event> findAllByIdIn(List<Long> eventIds);

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);
}
