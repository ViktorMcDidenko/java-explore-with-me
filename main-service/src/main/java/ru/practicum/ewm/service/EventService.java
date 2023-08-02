package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.dto.request.UpdateRequestStatus;
import ru.practicum.ewm.dto.request.UpdatedRequests;

import java.util.List;

public interface EventService {
    EventDto add(long userId, NewEventDto eventDto);

    EventDto getById(long userId, long eventId);

    List<EventShortDto> get(long userId, Pageable pageable);

    EventDto change(UpdateEventRequest updateEventRequest, long userId, long eventId);

    EventDto getByIdPublic(long id);

    List<EventShortDto> getPublic(GetEventsRequest request);

    EventDto changeAdmin(UpdateEventRequest updateEventRequest, long eventId);

    List<EventDto> getAdmin(GetEventsRequestAdmin request);

    List<RequestDto> getRequests(long userId, long eventId);

    UpdatedRequests updateRequestStatus(long userId, long eventId, UpdateRequestStatus update);
}