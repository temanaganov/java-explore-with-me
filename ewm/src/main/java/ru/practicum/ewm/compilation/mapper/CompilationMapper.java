package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation createCompilationDtoToCompilation(CreateCompilationDto createCompilationDto);
}
