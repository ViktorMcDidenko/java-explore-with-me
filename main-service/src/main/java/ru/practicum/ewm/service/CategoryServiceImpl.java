package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        if (categoryRepository.existsByNameAndIdIsNot(categoryDto.getName(), 0)) {
            throw new ConflictException(String.format("Name %s is already in use.", categoryDto.getName()));
        }
        Category category = mapper.categoryDtoToCategory(categoryDto);
        return saveAndReturn(category);
    }

    @Override
    public CategoryDto change(long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.", id)));
        if (categoryRepository.existsByNameAndIdIsNot(categoryDto.getName(), id)) {
            throw new ConflictException(String.format("Name %s is already in use.", categoryDto.getName()));
        }
        category.setName(categoryDto.getName());
        return saveAndReturn(category);
    }

    @Override
    public void delete(long id) {
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("The category is not empty.");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> get(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(mapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.", id)));
        return mapper.categoryToCategoryDto(category);
    }

    private CategoryDto saveAndReturn(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return mapper.categoryToCategoryDto(savedCategory);
    }
}