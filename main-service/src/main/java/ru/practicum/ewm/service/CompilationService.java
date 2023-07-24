package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto add(NewCompilationDto compilationDto);

    void delete(int compId);

    CompilationDto change(int compId, UpdateCompilationRequest request);

    CompilationDto getById(int compId);

    List<CompilationDto> get(boolean pinned, Pageable pageable);
}