package ru.practicum.ewm.model.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation newCompilationDtoToCompilation(NewCompilationDto compilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.isPinned());
        compilation.setEvents(events);
        return compilation;
    }

    public CompilationDto compilationToCompilationDto(Compilation compilation) {
        List<Event> eventList = new ArrayList<>(compilation.getEvents());
        List<EventShortDto> eventDtoList = eventMapper.eventToEventShortDtoList(eventList);
        return new CompilationDto(compilation.getId(), eventDtoList, compilation.isPinned(), compilation.getTitle());
    }
}