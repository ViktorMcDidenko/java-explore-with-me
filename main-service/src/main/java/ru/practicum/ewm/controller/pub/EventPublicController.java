package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.GetEventsRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.model.Sort;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPublicController {
    private final EventService service;
    private final StatsClient client;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/{id}")
    public EventDto readOne(HttpServletRequest request, @PathVariable long id) {
        try {
            client.post(request);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return service.getByIdPublic(id);
    }

    @GetMapping
    public List<EventShortDto> readAll(HttpServletRequest request,
                                       @RequestParam(required = false) String text,
                                       @RequestParam(required = false) List<Long> categories,
                                       @RequestParam(required = false) Boolean paid,
                                       @RequestParam(required = false) String rangeStart,
                                       @RequestParam(required = false) String rangeEnd,
                                       @RequestParam(required = false) Boolean onlyAvailable,
                                       @RequestParam(required = false) String sort,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        try {
            client.post(request);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        if (rangeStart != null) {
            try {
                start = LocalDateTime.parse(rangeStart, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Incorrectly made request.");
            }
        }
        if (rangeEnd != null) {
            try {
                end = LocalDateTime.parse(rangeEnd, FORMATTER);
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Incorrectly made request.");
            }
        }
        if (start.isAfter(end)) {
            throw new BadRequestException("End date for the search can not be before start date.");
        }
        if (categories == null) {
            categories = new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        GetEventsRequest eventsRequest = new GetEventsRequest(text,
                categories,
                paid,
                start,
                end,
                onlyAvailable,
                Sort.from(sort),
                pageable);
        return service.getPublic(eventsRequest);
    }
}