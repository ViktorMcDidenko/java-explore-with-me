package ru.practicum.ewm.client.stats;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class StatsClient {
    private final RestTemplate rest;
    private static final String APPLICATION = "ewm-main-service";
    private static final String STATS_SERVICE_URI = "http://localhost:9090";

    public StatsClient(RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(STATS_SERVICE_URI))
                .build();
    }

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
            rest.postForObject("/hit", requestEntity, EndpointHit.class);
        } catch (HttpStatusCodeException e) {
            throw new Exception(e.getStatusCode() + e.getResponseBodyAsString());
        }

    }

    public List<ViewStats> get(ViewsStatsRequest request) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = formatter.format(request.getStart());
        String end = formatter.format(request.getEnd());
        String path = "/stats" + String.format("?start=%s&end=%s", start, end);
        if (request.getUris() != null && !request.getUris().isEmpty()) {
            path += "&uris=" + String.join(",", request.getUris());
        }
        path += String.format("&unique=%b", request.isUnique());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<ViewStats[]> response = rest.exchange(path, HttpMethod.GET, requestEntity, ViewStats[].class);
            ViewStats[] result = response.getBody();
            if (result == null) {
                return  new ArrayList<>();
            } else {
                return List.of(result);
            }
        } catch (HttpStatusCodeException e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new Exception(e.getStatusCode() + e.getResponseBodyAsString());
        }
    }
}