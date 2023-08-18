package ru.practicum.ewm.dto.compilation;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Value
public class NewCompilationDto {
    Set<Long> events;
    boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    String title;
}