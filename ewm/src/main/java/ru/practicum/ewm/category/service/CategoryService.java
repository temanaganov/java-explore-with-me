package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CreateCategoryDto;
import ru.practicum.ewm.category.dto.UpdateCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.core.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository
                .findAll(pageable)
                .map(categoryMapper::categoryToCategoryDto)
                .toList();
    }

    public CategoryDto getCategoryById(long categoryId) {
        return categoryMapper.categoryToCategoryDto(checkCategory(categoryId));
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.createCategoryDtoToCategory(createCategoryDto);

        return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(long categoryId, UpdateCategoryDto updateCategoryDto) {
        Category category = checkCategory(categoryId);

        category.setName(updateCategoryDto.getName());

        return categoryMapper.categoryToCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(long categoryId) {
        checkCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private Category checkCategory(long categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category", categoryId));
    }
}
