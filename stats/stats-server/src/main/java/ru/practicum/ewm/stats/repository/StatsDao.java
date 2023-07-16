package ru.practicum.ewm.stats.repository;

import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.util.List;

public interface StatsDao {
    void saveHit(EndpointHit hit);

    List<ViewStats> getStats(ViewsStatsRequest request);
}