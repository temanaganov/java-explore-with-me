package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CreateCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(long userId, CreateCommentDto createCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(createCommentDto.getEventId());

        Comment comment = commentMapper.createCommentDtoToComment(createCommentDto);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(long userId, long commentId, UpdateCommentDto updateCommentDto) {
        checkUser(userId);
        Comment comment = checkComment(commentId);

        if (comment.getUser().getId() != userId) {
            throw new ConflictException("Only author of comment can edit it");
        }

        if (LocalDateTime.now().isAfter(comment.getCreatedOn().plusHours(1))) {
            throw new ConflictException("Comment editing is available within 1 hour after creation");
        }

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        return commentMapper.commentToCommentDto(comment);
    }

    @Transactional
    public CommentDto updateCommentByAdmin(long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = checkComment(commentId);

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        return commentMapper.commentToCommentDto(comment);
    }

    @Transactional
    public void deleteComment(long userId, long commentId) {
        checkUser(userId);
        Comment comment = checkComment(commentId);

        if (comment.getUser().getId() != userId) {
            throw new ConflictException("Only author of comment can delete it");
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkUser(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user", userId));
    }

    private Event checkEvent(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("event", eventId));
    }

    private Comment checkComment(long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException("comment", commentId));
    }
}
