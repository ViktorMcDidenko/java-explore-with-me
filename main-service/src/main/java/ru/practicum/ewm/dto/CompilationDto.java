package ru.practicum.ewm.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    int id;
    List<EventShortDto> events;
    boolean pinned;
    String title;
}