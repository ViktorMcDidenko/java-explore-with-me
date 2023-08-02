package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(CategoryDto categoryDto);

    CategoryDto change(long id, CategoryDto categoryDto);

    void delete(long id);

    List<CategoryDto> get(Pageable pageable);

    CategoryDto getById(long id);
}