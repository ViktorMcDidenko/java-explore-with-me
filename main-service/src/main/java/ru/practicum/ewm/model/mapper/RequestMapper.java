package ru.practicum.ewm.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public RequestDto requestToRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequestor().getId(),
                request.getStatus());
    }

    public List<RequestDto> requestToRequestDtoList(List<Request> requests) {
        return requests.stream().map(this::requestToRequestDto).collect(Collectors.toList());
    }
}