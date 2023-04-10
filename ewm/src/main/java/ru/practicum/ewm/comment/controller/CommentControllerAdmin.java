package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentControllerAdmin {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable long commentId, @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        return commentService.updateCommentByAdmin(commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
