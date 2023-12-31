package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.Status;
import ru.practicum.ewm.model.mapper.RequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    public RequestDto add(long userId, long eventId) {
        if (requestRepository.existsByRequestorIdAndEventId(userId, eventId)) {
            throw new ConflictException("You have already sent a request for this event.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", eventId)));
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException("This event is already at its maximum capacity.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("You can not send a request for this event.");
        }
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("You can not send a request for your own event.");
        }
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found.", userId)));
        Status status;
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            status = Status.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            status = Status.PENDING;
        }
        Request request = new Request(event, requestor, status);
        return saveAndReturn(request);
    }

    @Override
    public List<RequestDto> get(long userId) {
        List<Request> requests = requestRepository.findByRequestorId(userId);
        return requests.isEmpty() ? new ArrayList<>() : mapper.requestToRequestDtoList(requests);
    }

    @Override
    public RequestDto cancel(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found.", requestId)));
        if (request.getRequestor().getId() != userId) {
            throw new NotFoundException("You can not cancel this request.");
        }
        request.setStatus(Status.CANCELED);
        return saveAndReturn(request);
    }

    private RequestDto saveAndReturn(Request request) {
        Request savedRequest = requestRepository.save(request);
        return mapper.requestToRequestDto(savedRequest);
    }
}