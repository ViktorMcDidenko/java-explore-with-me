package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import ru.practicum.ewm.model.Status;

import java.time.LocalDateTime;

@Value
public class RequestDto {
    long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    long event;
    long requester;
    Status status;
}