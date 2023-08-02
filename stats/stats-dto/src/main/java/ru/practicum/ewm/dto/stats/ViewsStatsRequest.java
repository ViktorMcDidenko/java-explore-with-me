package ru.practicum.ewm.dto.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@Builder(toBuilder = true)
public class ViewsStatsRequest {
    private List<String> uris;
    @Builder.Default
    private LocalDateTime start = LocalDateTime.now().minusHours(1);
    @Builder.Default
    private LocalDateTime end = LocalDateTime.now().plusHours(1);
    private boolean unique;
    private String application;
}