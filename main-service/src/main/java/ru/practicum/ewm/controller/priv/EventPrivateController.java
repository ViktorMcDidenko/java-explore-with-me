package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService service;

    @PostMapping
    public EventDto create(@Valid @RequestBody NewEventDto eventDto, @PathVariable long userId) {
        return service.add(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto readOne(@PathVariable long userId, @PathVariable long eventId) {
        return service.getById(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> readAll(@PathVariable long userId,
                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return service.get(userId, pageable);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@Valid @RequestBody UpdateEventRequest updateEventRequest,
                           @PathVariable long userId,
                           @PathVariable long eventId) {
        return service.change(updateEventRequest, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> readRequests(@PathVariable long userId, @PathVariable long eventId) {
        return service.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public UpdatedRequests updateRequestsStatus(@RequestBody UpdateRequestStatus update,
                                                @PathVariable long userId,
                                                @PathVariable long eventId) {
        return service.updateRequestStatus(userId, eventId, update);
    }
}