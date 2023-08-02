package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto add(long userId, long eventId);

    List<RequestDto> get(long userId);

    RequestDto cancel(long userId, long requestId);
}