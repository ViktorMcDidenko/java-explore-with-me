package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient client;

    @Override
    public EventDto add(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("For the requested operation the conditions are not met.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found.", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.",
                        newEventDto.getCategory())));
        Event event = eventMapper.newEventDtotoEvent(newEventDto, category, user);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.eventToEventDto(savedEvent);
    }

    @Override
    public EventDto getById(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException(String
                    .format("Event with id=%d was not found or was not created by user with id=%d.", eventId, userId));
        }
        ViewsStatsRequest request = formViewsStatsRequest(Collections.singletonList("/events/" + eventId));
        try {
            List<ViewStats> stats = client.get(request);
            long views = stats.get(0).getHits();
            event.setViews(views);
            event = eventRepository.save(event);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return eventMapper.eventToEventDto(event);
    }

    @Override
    public List<EventShortDto> get(long userId, Pageable pageable) {
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        ViewsStatsRequest request = formViewsStatsRequest(uris);
        List<ViewStats> stats = new ArrayList<>();
        try {
            stats = client.get(request);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        if (!stats.isEmpty()) {
            Map<Long, Event> eventMap = events.stream().collect(Collectors.toMap(Event::getId, Function.identity()));
            for (ViewStats v : stats) {
                String[] statsArray = v.getUri().split("/");
                long id = Long.parseLong(statsArray[2]);
                Event event = eventMap.get(id);
                event.setViews(v.getHits());
                event = eventRepository.save(event);
                eventMap.put(id, event);
            }
            events = new ArrayList<>(eventMap.values());
        }
        return eventMapper.eventToEventShortDtoList(events);
    }

    @Override
    public EventDto change(UpdateEventRequest updateEventRequest, long userId, long eventId) {
        if (updateEventRequest.getEventDate() != null
                && updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("For the requested operation the conditions are not met.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", eventId)));
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed.");
        }
        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException(String
                    .format("Event with id=%d was not found or was not created by user with id=%d.", eventId, userId));
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found.", userId)));
        Category category = updateEventRequest.getCategory() != null ?
                categoryRepository.findById(updateEventRequest.getCategory())
                        .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.",
                                updateEventRequest.getCategory()))) : event.getCategory();
        Event eventToUpdate = eventMapper.updateEventUserRequestToEvent(updateEventRequest, event, user, category);
        Event updatedEvent = eventRepository.save(eventToUpdate);
        return eventMapper.eventToEventDto(updatedEvent);
    }

    @Override
    public EventDto getByIdPublic(long id) {
        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", id)));
        ViewsStatsRequest request = formViewsStatsRequest(Collections.singletonList("/events/" + id));
        try {
            List<ViewStats> stats = client.get(request);
            long views = stats.get(0).getHits();
            event.setViews(views);
            event = eventRepository.save(event);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return eventMapper.eventToEventDto(event);
    }

    @Override
    public List<EventShortDto> getPublic(GetEventsRequest eventsRequest) {
        List<Event> events = eventRepository.findPublic(eventsRequest.getText(),eventsRequest.getCategories(), eventsRequest.isPaid(),
                eventsRequest.getRangeStart(), eventsRequest.getRangeEnd(), eventsRequest.getPageable());
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        if (eventsRequest.isOnlyAvailable()) {
            events = events.stream().filter(e -> e.getParticipantLimit() == 0
                            || e.getParticipantLimit() > e.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        ViewsStatsRequest request = formViewsStatsRequest(uris);
        List<ViewStats> stats = new ArrayList<>();
        try {
            stats = client.get(request);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        if (!stats.isEmpty()) {
            Map<Long, Event> eventMap = events.stream().collect(Collectors.toMap(Event::getId, Function.identity()));
            for (ViewStats v : stats) {
                String[] statsArray = v.getUri().split("/");
                long id = Long.parseLong(statsArray[2]);
                Event event = eventMap.get(id);
                event.setViews(v.getHits());
                event = eventRepository.save(event); //если не работает, попробовать update через native query
                eventMap.put(id, event);
            }
            events = new ArrayList<>(eventMap.values());
        }
        if (eventsRequest.getSort().equals(Sort.VIEWS)) {
            events.sort(Comparator.comparingLong(Event::getViews));
        }
        return eventMapper.eventToEventShortDtoList(events);
    }

    @Override
    public EventDto changeAdmin(UpdateEventRequest updateEventRequest, long eventId) {
        if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("For the requested operation the conditions are not met.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", eventId)));
        if (event.getState() != State.PENDING) {
            throw new ConflictException("Only pending events can be changed.");
        }
        Category category = updateEventRequest.getCategory() != null ?
                categoryRepository.findById(updateEventRequest.getCategory())
                        .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.",
                                updateEventRequest.getCategory()))) : event.getCategory();
        Event eventToUpdate = eventMapper.updateEventUserRequestToEventAdmin(updateEventRequest, event, category);
        Event updatedEvent = eventRepository.save(eventToUpdate);
        return eventMapper.eventToEventDto(updatedEvent);
    }

    @Override
    public List<EventDto> getAdmin(GetEventsRequestAdmin request) {
        List<Event> events = eventRepository.findAdmin(request.getUsers(), request.getStates(), request.getCategories(),
                request.getRangeStart(), request.getRangeStart(), request.getPageable());
        return events.isEmpty() ? new ArrayList<>() : eventMapper.eventsToEventDtoList(events);
    }

    @Override
    public List<RequestDto> getRequests(long userId, long eventId) {
        List<Request> requests = requestRepository.findByEventId(eventId);
        if (requests.isEmpty()) {
            return new ArrayList<>();
        }
        if (requests.get(0).getEvent().getInitiator().getId() != userId) {
            throw new ConflictException("You are not allowed to inspect requests for this event.");
        }
        return requestMapper.requestToRequestDtoList(requests);
    }

    @Override
    public UpdatedRequests updateRequestStatus(long userId, long eventId, UpdateRequestStatus update) {
        Status status = Status.from(update.getStatus())
                .orElseThrow(() -> new BadRequestException("Unknown state: " + update.getStatus()));
        List<Request> requests = requestRepository.findAllById(update.getRequestIds());
        if (requests.isEmpty()) {
            throw new NotFoundException("Requests you are trying to update are not found.");
        }
        requests.stream().filter(r -> !r.getState().equals(State.PENDING)).forEach(r -> {
            throw new ConflictException("Request must have status PENDING");
        });
        requests.stream().filter(r -> r.getEvent().getId() != eventId).forEach(r -> {
            throw new ConflictException("Requests you are trying to update are not for the event with id=" + eventId);
        });
        Event event = requests.get(0).getEvent();
        if (event.getInitiator().getId() != userId) {
            throw new ConflictException("You are not allowed to inspect requests for this event.");
        }
        if (status.equals(Status.REJECTED) || (event.getParticipantLimit() != 0
                && event.getParticipantLimit() + update.getRequestIds().size() <= event.getConfirmedRequests())) {
            for (Request r : requests) {
                r.setState(State.CANCELLED);
                requestRepository.save(r);
            }
            return new UpdatedRequests(null, requestMapper.requestToRequestDtoList(requests));
        }
        for (Request r : requests) {
            r.setState(State.PUBLISHED);
            requestRepository.save(r);
        }
        event.setConfirmedRequests(event.getConfirmedRequests() + requests.size());
        eventRepository.save(event);
        return new UpdatedRequests(requestMapper.requestToRequestDtoList(requests), null);
    }

    private ViewsStatsRequest formViewsStatsRequest(List<String> uris) {
        return ViewsStatsRequest.builder()
                .uris(uris)
                .application("ewm-main-service")
                .build();
    }
}