package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.AdminCommentDto;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto add(long userId, long eventId, NewCommentDto commentDto) {
        Event event = eventRepository.findByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found.", eventId)));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found.", userId)));
        Role role = Role.USER;
        if (userId == event.getInitiator().getId()) {
            role = Role.INITIATOR;
        } else if (requestRepository.existsByRequestorIdAndEventIdAndStatus(userId, eventId, Status.CONFIRMED)) {
            role = Role.PARTICIPANT;
        }
        Comment comment = new Comment(commentDto.getText(), event, author, role);
        Comment savedComment = commentRepository.save(comment);
        return mapper.commentToCommentDto(savedComment);
    }

    @Override
    public CommentDto report(long userId, long eventId, long commentId) {
        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id=%d was not found.", eventId));
        }
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found.", userId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found.", commentId)));
        if (!comment.isReported()) {
            comment.setReported(true);
        }
        Set<User> reportedBy = comment.getReportedBy() == null ? new HashSet<>() : comment.getReportedBy();
        reportedBy.add(reporter);
        comment.setReportedBy(reportedBy);
        Comment savedComment = commentRepository.save(comment);
        return mapper.commentToCommentDto(savedComment);
    }

    @Override
    public void delete(long userId, long eventId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found.", commentId)));
        if (comment.getEvent().getId() != eventId) {
            throw new ConflictException("There are no event with comment with id=" + commentId);
        }
        if (comment.getAuthor().getId() != userId) {
            throw new ConflictException("You can not delete comment with id=" + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> get(long eventId, Pageable pageable) {
        List<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return mapper.commentToCommentDtoList(comments);
    }

    @Override
    public List<AdminCommentDto> getReported(List<Long> events, Pageable pageable) {
        if (!eventRepository.existsByIdIn(events)) {
            return new ArrayList<>();
        }
        List<Comment> comments = commentRepository.findReported(events, pageable);
        return mapper.commentToAdminCommentDtoList(comments);
    }

    @Override
    public AdminCommentDto unreport(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d was not found.", commentId)));
        comment.setReported(false);
        comment.setReportedBy(null);
        Comment savedComment = commentRepository.save(comment);
        return mapper.commentToAdminCommentDto(savedComment);
    }

    @Override
    public void deleteByAdmin(long commentId) {
        commentRepository.deleteById(commentId);
    }
}