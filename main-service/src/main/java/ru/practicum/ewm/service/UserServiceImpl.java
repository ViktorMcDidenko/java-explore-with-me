package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto add(UserDto userDto) {
        if (repository.existsByName(userDto.getName())) {
            throw new ConflictException(String.format("Name %s is already in use.", userDto.getName()));
        }
        User user = mapper.userDtoToUser(userDto);
        User savedUser = repository.save(user);
        return mapper.userToUserDto(savedUser);
    }

    @Override
    public List<UserDto> get(Set<Long> ids, Pageable pageable) {
        return repository.findByIdIn(ids, pageable).stream().map(mapper::userToUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }
}