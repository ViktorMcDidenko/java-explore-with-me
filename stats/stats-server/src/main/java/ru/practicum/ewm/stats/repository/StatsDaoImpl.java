package ru.practicum.ewm.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsDaoImpl implements StatsDao {
    private final JdbcTemplate jdbc;

    @Override
    public void saveHit(EndpointHit hit) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO stats (app, uri, ip, created) VALUES (?, ?, ?, ?)",
                            new String[]{"id"});
            ps.setString(1, hit.getApp());
            ps.setString(2, hit.getUri());
            ps.setString(3, hit.getIp());
            ps.setTimestamp(4, Timestamp.valueOf(hit.getTimestamp()));
            return ps;
        }, keyHolder);
    }

    @Override
    public List<ViewStats> getStats(ViewsStatsRequest request) {
        String sql = "SELECT s.app, s.uri, ";
        sql += request.isUnique() ? "COUNT (DISTINCT s.ip) AS hits " : "COUNT (s.ip) AS hits ";
        sql += "FROM Stats s " +
                "WHERE (s.created >= ? AND s.created <= ?) ";
        if (request.getUris().isEmpty()) {
            sql += "AND s.uri IN ? " +
                    "GROUP BY s.app, s.uri";
            return jdbc.query(sql, rowMapper(), request.getStart(), request.getEnd(), request.getUris());
        }
        sql += "GROUP BY s.app, s.uri";
        return jdbc.query(sql, rowMapper(), request.getStart(), request.getEnd());
    }

    private RowMapper<ViewStats> rowMapper() {
        return (rs, rowNum) -> ViewStats.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getInt("hits"))
                .build();
    }
}