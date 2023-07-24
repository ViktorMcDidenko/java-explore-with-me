package ru.practicum.ewm.model;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;

@Component
public class UserMapper {
    public UserDto userToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public User userDtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }

    public UserShortDto userToUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}