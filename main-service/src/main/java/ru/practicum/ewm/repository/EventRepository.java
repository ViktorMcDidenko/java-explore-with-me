package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryId(Long catId);

    List<Event> findByInitiatorId(long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndState(long id, State state);

    Set<Event> findByIdIn(Set<Long> events);

    @Query(value = "SELECT * " +
            "FROM events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE lower(CONCAT('%', :text, '%'))) " +
            "AND e.category_id IN (:categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.event_date >= :rangeStart AND e.event_date <= :rangeEnd) " +
            "ORDER BY e.event_date", nativeQuery = true)
    List<Event> findPublic(@Param("text") String text,
                           @Param("categories") List<Long> categories,
                           @Param("paid") Boolean paid,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE lower(CONCAT('%', :text, '%'))) " +
            "AND e.category_id IN (:categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.event_date >= :rangeStart AND e.event_date <= :rangeEnd) " +
            "AND (e.participant_limit = 0 OR e.participant_limit > e.requests)" +
            "ORDER BY e.event_date", nativeQuery = true)
    List<Event> findPublicOnlyAvailable(@Param("text") String text,
                           @Param("categories") List<Long> categories,
                           @Param("paid") Boolean paid,
                           @Param("rangeStart") LocalDateTime rangeStart,
                           @Param("rangeEnd") LocalDateTime rangeEnd,
                           Pageable pageable);

    @Query(value = "SELECT * FROM events e " +
            "WHERE (:users IS NULL OR e.initiator_id IN (CAST(CAST(:users AS TEXT) AS BIGINT))) " +
            "AND (:states IS NULL OR e.state IN (CAST(:states AS TEXT))) " +
            "AND (:categories IS NULL OR e.category_id IN (CAST(CAST(:categories AS TEXT) AS BIGINT))) " +
            "AND (CAST(:rangeStart AS TIMESTAMP) IS NULL OR e.event_date >= CAST(:rangeStart AS TIMESTAMP)) " +
            "AND (CAST(:rangeEnd AS TIMESTAMP)  IS NULL OR e.event_date < CAST(:rangeEnd AS TIMESTAMP)) ",
            nativeQuery = true)
    List<Event> findAdmin(@Param("users") List<Long> users,
                          @Param("states") List<String> states,
                          @Param("categories") List<Long> categories,
                          @Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd,
                          Pageable pageable);
}