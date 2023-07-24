package ru.practicum.ewm.dto;

import lombok.Value;

import java.util.List;

@Value
public class CompilationDto {
    int id;
    List<EventShortDto> events;
    boolean pinned;
    String title;
}