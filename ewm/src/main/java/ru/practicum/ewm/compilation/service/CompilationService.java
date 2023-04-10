package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> getAllCompilations(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        return compilations
                .stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilationById(long compilationId) {
        Compilation compilation = compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException("compilation", compilationId));

        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Transactional
    public CompilationDto createCompilation(CreateCompilationDto createCompilationDto) {
        Compilation compilation = compilationMapper.createCompilationDtoToCompilation(createCompilationDto);

        Set<Event> events = eventRepository.findAllByIdIn(createCompilationDto.getEvents());

        compilation.setEvents(events);

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Transactional
    public CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException("compilation", compilationId));

        compilationMapper.updateCompilation(compilation, updateCompilationDto);

        if (updateCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationDto.getEvents()));
        }

        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Transactional
    public void deleteCompilation(long compilationId) {
        compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException("compilation", compilationId));

        compilationRepository.deleteById(compilationId);
    }
}
