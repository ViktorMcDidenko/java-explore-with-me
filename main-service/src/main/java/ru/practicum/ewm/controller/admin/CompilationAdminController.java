package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationService service;

    @PostMapping
    public CompilationDto create(@Valid @RequestBody NewCompilationDto compilationDto) {
        return service.add(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable int compId) {
        service.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@Valid @RequestBody UpdateCompilationRequest request, @PathVariable int compId) {
        return service.change(compId, request);
    }
}