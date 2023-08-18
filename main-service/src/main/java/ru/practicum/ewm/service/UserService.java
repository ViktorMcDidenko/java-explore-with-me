package ru.practicum.ewm.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> get(Set<Long> ids, Pageable pageable);

    void delete(long id);
}