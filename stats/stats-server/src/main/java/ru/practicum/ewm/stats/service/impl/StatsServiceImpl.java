package ru.practicum.ewm.stats.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;
import ru.practicum.ewm.stats.repository.StatsDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsDao repository;

    @Override
    public void saveHit(EndpointHit hit) {
        repository.saveHit(hit);
    }

    @Override
    public List<ViewStats> calculateViews(ViewsStatsRequest request) {
        return repository.getStats(request);
    }
}