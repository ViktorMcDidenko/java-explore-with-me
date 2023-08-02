package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequestorIdAndEventId(long requestorId, long eventId);

    List<Request> findByRequestorId(long requestorId);

    List<Request> findByEventId(long eventId);
}