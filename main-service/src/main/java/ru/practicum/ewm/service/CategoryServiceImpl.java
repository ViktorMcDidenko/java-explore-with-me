package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto add(CategoryDto categoryDto) {
        Category category = mapper.categoryDtoToCategory(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return mapper.categoryToCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto change(long id, CategoryDto categoryDto) {
        Category savedCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.", id)));
        savedCategory.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(savedCategory); //если не сработает, сначала удалить, потом сохранить заново, без приблуд выше
        return mapper.categoryToCategoryDto(updatedCategory);
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
        Page<Category> page = categoryRepository.findAll(pageable);
        List<CategoryDto> result = new ArrayList<>();
        for (Category c : page) { //не факт, что это работает
            result.add(mapper.categoryToCategoryDto(c));
        }
        return result;
    }

    @Override
    public CategoryDto getById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.", id)));
        return mapper.categoryToCategoryDto(category);
    }
}