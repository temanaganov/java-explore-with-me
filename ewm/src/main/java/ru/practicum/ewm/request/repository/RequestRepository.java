package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(long requesterId);

    List<Request> findAllByEventIdAndEventInitiatorId(long eventId, long initiatorId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.event.id = :eventId AND " +
            "r.event.initiator.id = :initiatorId AND " +
            "r.id in :requestIds " +
            "ORDER BY r.created ASC")
    List<Request> findRequestsForUpdating(
            @Param("eventId") long eventId,
            @Param("initiatorId") long initiatorId,
            @Param("requestIds") List<Long> requestIds
    );

    Optional<Request> findByRequesterIdAndEventId(long requesterId, long eventId);

    @Query("SELECT count(r) FROM Request r " +
            "WHERE r.event.id = :eventId AND r.status = 'CONFIRMED'")
    long findCountOfEventConfirmedRequests(@Param("eventId") long eventId);

    @Query("SELECT r FROM Request r " +
            "WHERE r.event.id IN :eventId AND r.status = 'CONFIRMED'")
    List<Request> findAllConfirmedByEventIdIn(List<Long> eventId);
}
