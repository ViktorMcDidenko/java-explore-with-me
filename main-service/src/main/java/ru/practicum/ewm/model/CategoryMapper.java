package ru.practicum.ewm.model;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CategoryDto;

@Component
public class CategoryMapper {
    public CategoryDto categoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }
}