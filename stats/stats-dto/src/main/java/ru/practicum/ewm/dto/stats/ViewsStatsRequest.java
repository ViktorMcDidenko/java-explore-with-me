package ru.practicum.ewm.dto.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@ToString
@Builder(toBuilder = true)
@Entity
public class ViewsStatsRequest {
    @Singular("uri")
    private Set<String> uris;
    @Builder.Default
    private LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    @Builder.Default
    private LocalDateTime end = LocalDateTime.now();
    private boolean unique;
    private String application;
}