package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CreateCommentDto;
import ru.practicum.ewm.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment createCommentDtoToComment(CreateCommentDto createCommentDto);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "userId", source = "user.id")
    CommentDto commentToCommentDto(Comment comment);
}
