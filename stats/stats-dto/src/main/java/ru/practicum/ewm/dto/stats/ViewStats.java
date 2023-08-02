package ru.practicum.ewm.dto.stats;

import lombok.*;

@Data
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private int hits;
}