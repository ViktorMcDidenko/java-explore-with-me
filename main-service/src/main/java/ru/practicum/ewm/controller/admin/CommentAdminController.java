package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.AdminCommentDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events/comment")
@RequiredArgsConstructor
@Validated
public class CommentAdminController {
    private final CommentService service;

    @GetMapping
    public List<AdminCommentDto> getReported(@RequestParam(required = false) List<Long> events,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (events == null) {
            events = new ArrayList<>();
        }
        return service.getReported(events, pageable);
    }

    @PatchMapping("/{commentId}")
    public AdminCommentDto unreport(@PathVariable long commentId) {
        return service.unreport(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) {
        service.deleteByAdmin(commentId);
    }
}