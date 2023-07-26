package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.RequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService service;

    @PostMapping
    public RequestDto create(@PathVariable long userId, @RequestParam long eventId) {
        return service.add(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> readAll(@PathVariable long userId) {
        return service.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancel(@PathVariable long userId,
                             @PathVariable long requestId) {
        return service.cancel(userId, requestId);
    }
}