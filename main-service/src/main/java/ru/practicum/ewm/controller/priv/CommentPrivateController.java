package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.NewCommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/comment")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {
    private final CommentService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto create(@Valid @RequestBody NewCommentDto commentDto,
                             @PathVariable long userId,
                             @PathVariable long eventId) {
        return service.add(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto report(@PathVariable long userId,
                             @PathVariable long eventId,
                             @PathVariable long commentId) {
        return service.report(userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId,
                       @PathVariable long eventId,
                       @PathVariable long commentId) {
        service.delete(userId, eventId, commentId);
    }
}