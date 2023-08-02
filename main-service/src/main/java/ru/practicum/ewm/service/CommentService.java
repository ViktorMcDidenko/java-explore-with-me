package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.AdminCommentDto;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto add(long userId, long eventId, NewCommentDto commentDto);

    CommentDto report(long userId, long eventId, long commentId);

    void delete(long userId, long eventId, long commentId);

    List<CommentDto> get(long eventId, Pageable pageable);

    List<AdminCommentDto> getReported(List<Long> events, Pageable pageable);

    AdminCommentDto unreport(long commentId);

    void deleteByAdmin(long commentId);
}