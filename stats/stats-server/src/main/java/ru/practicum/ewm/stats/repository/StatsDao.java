package ru.practicum.ewm.stats.repository;

import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsDao {
    void saveHit(EndpointHit hit);
    List<ViewStats> getStats(ViewsStatsRequest request);
}