package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GetEventsRequest {
    String text;
    List<Long> categories;
    boolean paid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;
    boolean onlyAvailable;
    Sort sort;
    Pageable pageable;
}