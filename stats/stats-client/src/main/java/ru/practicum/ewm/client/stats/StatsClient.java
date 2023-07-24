package ru.practicum.ewm.client.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewStatsList;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate rest;
    private static final String APPLICATION = "ewm-main-service";
    private static final String STATS_SERVICE_URI = "http://localhost:9090";

    public void post(HttpServletRequest request) throws Exception {
        EndpointHit hit = EndpointHit.builder()
                .app(APPLICATION)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(hit, headers);
        try {
            rest.postForObject(STATS_SERVICE_URI + "/hit", requestEntity, EndpointHit.class);
        } catch (HttpStatusCodeException e) {
            throw new Exception(e.getStatusCode() + e.getResponseBodyAsString());
        }

    }

    public List<ViewStats> get(ViewsStatsRequest request) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = URLEncoder.encode(formatter.format(request.getStart()));
        String end = URLEncoder.encode(formatter.format(request.getEnd()));
        String path = STATS_SERVICE_URI + "/stats" + String.format("?start=%s&end=%s", start, end);
        if (request.getUris() != null && !request.getUris().isEmpty()) {
            path += "&uris=" + String.join(",", request.getUris());
        }
        path += String.format("&unique=%b", request.isUnique());
        try {
            ViewStatsList response = rest.getForObject(path, ViewStatsList.class);
            return response == null ? new ArrayList<>() : response.getViewStatsList();
        } catch (HttpStatusCodeException e) {
            throw new Exception(e.getStatusCode() + e.getResponseBodyAsString());
        }
    }
}