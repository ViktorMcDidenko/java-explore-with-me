package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @GetMapping
    public List<UserDto> read(@RequestParam (name = "ids", required = false) Set<Long> ids,
                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return service.get(ids, pageable);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        service.delete(userId);
    }
}