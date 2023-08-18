package ru.practicum.ewm.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.model.Category;

@Component
public class CategoryMapper {
    public CategoryDto categoryToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category categoryDtoToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }
}