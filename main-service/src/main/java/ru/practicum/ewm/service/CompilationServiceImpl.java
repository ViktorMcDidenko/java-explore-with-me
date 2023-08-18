package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.mapper.CompilationMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    public CompilationDto add(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findByIdIn(compilationDto.getEvents());
        }
        Compilation compilation = mapper.newCompilationDtoToCompilation(compilationDto, events);
        return saveAndReturn(compilation);
    }

    @Override
    public void delete(int compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto change(int compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d was not found.", compId)));
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            Set<Event> events = eventRepository.findByIdIn(request.getEvents());
            compilation.setEvents(events);
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        return saveAndReturn(compilation);
    }

    @Override
    public CompilationDto getById(int compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id=%d was not found.", compId)));
        return mapper.compilationToCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> get(boolean pinned, Pageable pageable) {
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, pageable);
        return compilations.stream().map(mapper::compilationToCompilationDto).collect(Collectors.toList());
    }

    private CompilationDto saveAndReturn(Compilation compilation) {
        Compilation savedCompilation = compilationRepository.save(compilation);
        return mapper.compilationToCompilationDto(savedCompilation);
    }
}