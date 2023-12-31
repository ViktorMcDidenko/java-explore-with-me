package ru.practicum.ewm.dto.event;

import lombok.Value;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GetEventsRequestAdmin {
    List<Long> users;
    List<String> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Pageable pageable;
}