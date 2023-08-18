package ru.practicum.ewm.dto.compilation;

import lombok.Value;

import javax.validation.constraints.Size;
import java.util.Set;

@Value
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}