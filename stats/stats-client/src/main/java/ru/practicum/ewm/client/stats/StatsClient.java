package ru.practicum.ewm.client.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate rest;
    private static final String APPLICATION = "ewm-main-service";
    private static final String STATS_SERVICE_URI = "http://localhost:9090";

    public ResponseEntity<Object> post(HttpServletRequest request) {
        EndpointHit hit = EndpointHit.builder()
                .app(APPLICATION)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        String path = STATS_SERVICE_URI + "/hit";
        return makeAndSendRequest(HttpMethod.POST, path, null, hit);
    }

    public ResponseEntity<Object> get(ViewsStatsRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = URLEncoder.encode(formatter.format(request.getStart()));
        String end = URLEncoder.encode(formatter.format(request.getEnd()));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (request.getUris() != null && !request.getUris().isEmpty()) {
            parameters.put("uris", request.getUris());
        }
        parameters.put("unique", request.isUnique());
        String path = STATS_SERVICE_URI + "/stats";
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                      String path,
                                                      @Nullable Map<String, Object> parameters,
                                                      @Nullable EndpointHit body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Object> statsServerResponse;
        try {
            if (parameters != null) {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareResponse(statsServerResponse);
    }

    private static ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}