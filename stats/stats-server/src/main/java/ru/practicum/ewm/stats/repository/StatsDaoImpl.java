package ru.practicum.ewm.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsDaoImpl implements StatsDao {
    private final JdbcTemplate jdbc;

    @Override
    public void saveHit(EndpointHit hit) {
        jdbc.update("INSERT INTO stats (app, uri, ip, created) VALUES (?, ?, ?, ?)",
                hit.getApp(), hit.getUri(), hit.getIp(), Timestamp.valueOf(hit.getTimestamp()));
    }

    @Override
    public List<ViewStats> getStats(ViewsStatsRequest request) {
        String sql = "SELECT app, uri, ";
        sql += request.isUnique() ? "COUNT (DISTINCT ip) AS hits " : "COUNT (ip) AS hits ";
        sql += "FROM stats WHERE (created >= ? AND created <= ?) ";
        if (!request.getUris().isEmpty()) {
            sql += addUris(request.getUris());
        }
        sql += "GROUP BY app, uri ORDER BY hits DESC";
        return jdbc.query(sql, rowMapper(), request.getStart(), request.getEnd());
    }

    private RowMapper<ViewStats> rowMapper() {
        return (rs, rowNum) -> {
            ViewStats result = ViewStats.builder()
                    .app(rs.getString("app"))
                    .uri(rs.getString("uri"))
                    .hits(rs.getInt("hits"))
                    .build();
            return result;
        };
    }

    private String addUris(List<String> uris) {
        String result = "AND uri IN ('";
        for (int i = 0; i < uris.size(); i++) {
            if (i > 0) {
                result += ", '";
            }
            result += uris.get(i) + "'";
        }
        return result + ") ";
    }
}