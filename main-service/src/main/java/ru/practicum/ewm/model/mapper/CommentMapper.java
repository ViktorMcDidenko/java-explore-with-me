package ru.practicum.ewm.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.AdminCommentDto;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CommentMapper {
    public CommentDto commentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getRole(),
                comment.getCreated(),
                comment.isReported());
    }

    public AdminCommentDto commentToAdminCommentDto(Comment comment) {
        Set<Long> reportedBy = new HashSet<>();
        if (comment.getReportedBy() != null) {
            reportedBy = comment.getReportedBy().stream().map(User::getId).collect(Collectors.toSet());
        }
        return new AdminCommentDto(comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getRole(),
                comment.getCreated(),
                comment.isReported(),
                reportedBy);
    }

    public List<CommentDto> commentToCommentDtoList(List<Comment> comments) {
        return comments.stream().map(this::commentToCommentDto).collect(Collectors.toList());
    }

    public List<AdminCommentDto> commentToAdminCommentDtoList(List<Comment> comments) {
        return comments.stream().map(this::commentToAdminCommentDto).collect(Collectors.toList());
    }
}