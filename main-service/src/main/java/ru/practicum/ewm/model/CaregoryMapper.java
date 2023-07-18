package ru.practicum.ewm.model;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CategoryDto;

@Mapper
public interface CaregoryMapper {
    CategoryDto categoryToCategoryDto(Category category);
    Category categoryDtoToCategory(CategoryDto categoryDto);
}