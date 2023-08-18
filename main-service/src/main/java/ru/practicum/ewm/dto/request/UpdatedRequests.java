package ru.practicum.ewm.dto.request;

import lombok.Value;
import ru.practicum.ewm.dto.request.RequestDto;

import java.util.List;

@Value
public class UpdatedRequests {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}