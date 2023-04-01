package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    public CompilationDto createCompilation(CreateCompilationDto createCompilationDto) {
        Compilation compilation = compilationMapper.createCompilationDtoToCompilation(createCompilationDto);

        List<Event> events = createCompilationDto
                .getEvents()
                .stream()
                .map(eventId -> eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("event", eventId)))
                .collect(Collectors.toList());

        compilation.setEvents(events);

        compilation = compilationRepository.save(compilation);

        compilation.getId();

        return compilationMapper.compilationToCompilationDto(compilation);
    }

    public CompilationDto updateCompilation(long compilationId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException("compilation", compilationId));

        if (updateCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationDto.getEvents()));
        }

        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    public void deleteCompilation(long compilationId) {
        compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException("compilation", compilationId));

        compilationRepository.deleteById(compilationId);
    }
}
