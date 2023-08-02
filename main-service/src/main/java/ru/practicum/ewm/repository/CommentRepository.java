package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(long eventId, Pageable pageable);

    @Query(value = "SELECT * " +
            "FROM comments c " +
            "WHERE c.reported = TRUE " +
            "AND (:events IS NULL OR c.event_id IN (CAST(CAST(:events AS TEXT) AS BIGINT))) ", nativeQuery = true)
    List<Comment> findReported(@Param("events") List<Long> events, Pageable pageable);
}