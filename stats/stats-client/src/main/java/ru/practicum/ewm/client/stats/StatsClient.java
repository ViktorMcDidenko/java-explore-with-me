package ru.practicum.ewm.client.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class StatsClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String application;
    private final String statsServiceUri;
    private final ObjectMapper json;
    private final HttpClient httpClient;

    public StatsClient(@Value("ewm-main-service") String application,
                       @Value("http://localhost:9090") String statsServiceUri,
                       ObjectMapper json) {
        this.application = application;
        this.statsServiceUri = statsServiceUri;
        this.json = json;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    }

    public void hit(HttpServletRequest userRequest) throws Exception {
        EndpointHit hit = EndpointHit.builder()
                .app(application)
                .ip(userRequest.getRemoteAddr())
                .uri(userRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        try {
            HttpRequest.BodyPublisher bodyPublisher = HttpRequest
                    .BodyPublishers
                    .ofString(json.writeValueAsString(hit));
            HttpRequest hitRequest = HttpRequest.newBuilder()
                    .uri(URI.create(statsServiceUri + "/hit"))
                    .POST(bodyPublisher)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<Void> response = httpClient.send(hitRequest, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public List<ViewStats> getStats(ViewsStatsRequest request) throws Exception {
        try {
            String queryString = toQueryString(
                    request.toBuilder().application(application).build()
            );
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(statsServiceUri + "/stats" + queryString))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                return json.readValue(response.body(), new TypeReference<>() {
                });
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return Collections.emptyList();
    }

    private String toQueryString(ViewsStatsRequest request) {
        String start = encode(formatter.format(request.getStart()));
        String end = encode(formatter.format(request.getEnd()));
        String queryString = String.format("?start=%s&end=%s", start, end);
        if (request.getUris() != null && !request.getUris().isEmpty()) {
            queryString += "&uris=" + String.join(",", request.getUris());
        }
        queryString += String.format("unique=%b", request.isUnique());
        return queryString;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}