package ru.practicum.ewm.dto.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ViewStats {
    String app;
    String uri;
    int hits;
}