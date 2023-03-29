package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CreateCategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category createCategoryDtoToCategory(CreateCategoryDto createCategoryDto);

    CategoryDto categoryToCategoryDto(Category category);
}
