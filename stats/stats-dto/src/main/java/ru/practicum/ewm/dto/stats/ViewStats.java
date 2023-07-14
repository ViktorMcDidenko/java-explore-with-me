package ru.practicum.ewm.dto.stats;

import lombok.Builder;

import javax.persistence.Entity;

@Builder
@Entity
public class ViewStats {
    String app;
    String uri;
    int hits;
}