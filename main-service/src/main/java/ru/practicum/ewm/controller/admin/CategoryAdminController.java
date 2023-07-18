package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @Validated
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.add(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto, @PathVariable long id) {
        return categoryService.change(id, categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable long id) {
        categoryService.delete(id);
    }
}