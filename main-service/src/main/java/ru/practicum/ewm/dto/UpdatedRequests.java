package ru.practicum.ewm.dto;

import lombok.Value;

import java.util.List;

@Value
public class UpdatedRequests {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}