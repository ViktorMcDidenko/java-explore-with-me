package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventDto;
import ru.practicum.ewm.dto.event.GetEventsRequestAdmin;
import ru.practicum.ewm.dto.event.UpdateEventRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService service;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PatchMapping("/{eventId}")
    public EventDto update(@Valid @RequestBody UpdateEventRequest updateEventRequest,
                           @PathVariable long eventId) {
        return service.changeAdmin(updateEventRequest, eventId);
    }

    @GetMapping
    public List<EventDto> read(@RequestParam(required = false) List<Long> users,
                               @RequestParam(required = false) List<String> states,
                               @RequestParam(required = false) List<Long> categories,
                               @RequestParam(required = false) String rangeStart,
                               @RequestParam(required = false) String rangeEnd,
                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        LocalDateTime start = LocalDateTime.of(2000, 01, 01, 00, 00, 00);
        LocalDateTime end = LocalDateTime.of(9999, 12, 12, 23, 59, 59);
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
        if (categories == null) {
            categories = new ArrayList<>();
        }
        if (users == null) {
            users = new ArrayList<>();
        }
        if (states != null) {
            states = new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        GetEventsRequestAdmin request = new GetEventsRequestAdmin(users,
                states,
                categories,
                start,
                end,
                pageable);
        return service.getAdmin(request);
    }
}