package ru.practicum.ewm.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private int id;
    private List<EventShortDto> events;
    private boolean pinned;
    private String title;
}