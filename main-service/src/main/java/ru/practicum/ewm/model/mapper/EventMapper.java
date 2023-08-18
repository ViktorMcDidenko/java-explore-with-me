package ru.practicum.ewm.model.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.EventDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventRequest;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.State;
import ru.practicum.ewm.model.enums.StateAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event newEventDtotoEvent(NewEventDto newEventDto, Category category, User user) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(user);
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(State.PENDING);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public EventDto eventToEventDto(Event event) {
        CategoryDto category = categoryMapper.categoryToCategoryDto(event.getCategory());
        UserShortDto initiator = userMapper.userToUserShortDto(event.getInitiator());
        Location location = new Location(event.getLat(), event.getLon());
        return new EventDto(event.getId(),
                event.getAnnotation(),
                category,
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                initiator,
                location,
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews());
    }

    public List<EventShortDto> eventToEventShortDtoList(List<Event> events) {
        return events.stream().map(e -> new EventShortDto(e.getId(),
                e.getAnnotation(),
                categoryMapper.categoryToCategoryDto(e.getCategory()),
                e.getConfirmedRequests(),
                e.getEventDate(),
                userMapper.userToUserShortDto(e.getInitiator()),
                e.isPaid(),
                e.getTitle(),
                e.getViews())).collect(Collectors.toList());
    }

    public Event updateEventUserRequestToEvent(UpdateEventRequest request, Event oldEvent, User initiator,
                                               Category category) {
        State state = oldEvent.getState();
        if (request.getStateAction() != null) {
            if (StateAction.CANCEL_REVIEW.name().equalsIgnoreCase(request.getStateAction())) {
                state = State.CANCELED;
            } else if (StateAction.SEND_TO_REVIEW.name().equalsIgnoreCase(request.getStateAction())) {
                state = State.PENDING;
            }
        }
        return formUpdatedEvent(request, oldEvent, initiator, category, state, oldEvent.getPublishedOn());
    }

    public Event updateEventUserRequestToEventAdmin(UpdateEventRequest request, Event oldEvent, Category category) {
        LocalDateTime publishedOn = oldEvent.getPublishedOn();
        State state = oldEvent.getState();
        if (request.getStateAction() != null) {
            if (StateAction.REJECT_EVENT.name().equalsIgnoreCase(request.getStateAction())) {
                state = State.CANCELED;
            } else if (StateAction.PUBLISH_EVENT.name().equalsIgnoreCase(request.getStateAction())) {
                state = State.PUBLISHED;
                publishedOn = LocalDateTime.now();
            }
        }
        return formUpdatedEvent(request, oldEvent, oldEvent.getInitiator(), category, state, publishedOn);
    }

    public List<EventDto> eventsToEventDtoList(List<Event> events) {
        return events.stream().map(this::eventToEventDto).collect(Collectors.toList());
    }

    private Event formUpdatedEvent(UpdateEventRequest request, Event oldEvent, User initiator, Category category,
                                   State state, LocalDateTime publishedOn) {
        String annotation = request.getAnnotation() != null ? request.getAnnotation() : oldEvent.getAnnotation();
        String description = request.getDescription() != null ? request.getDescription() : oldEvent.getDescription();
        LocalDateTime eventDate = request.getEventDate() != null ? request.getEventDate() : oldEvent.getEventDate();
        boolean paid = request.getPaid() != null ? request.getPaid() : oldEvent.isPaid();
        int participantLimit = request.getParticipantLimit() != null ? request.getParticipantLimit() :
                oldEvent.getParticipantLimit();
        boolean requestModeration = request.getRequestModeration() != null ? request.getRequestModeration() :
                oldEvent.isRequestModeration();
        String title = request.getTitle() != null ? request.getTitle() : oldEvent.getTitle();
        float lat;
        float lon;
        if (request.getLocation() != null) {
            lat = request.getLocation().getLat();
            lon = request.getLocation().getLon();
        } else {
            lat = oldEvent.getLat();
            lon = oldEvent.getLon();
        }
        return new Event(oldEvent.getId(),
                annotation,
                category,
                oldEvent.getConfirmedRequests(),
                oldEvent.getCreatedOn(),
                description,
                eventDate,
                initiator,
                lat,
                lon,
                paid,
                participantLimit,
                publishedOn,
                requestModeration,
                state,
                title,
                oldEvent.getViews()
        );
    }
}