package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.core.exception.NotFoundException;
import ru.practicum.ewm.user.dto.CreateUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers(List<Long> userIds, Pageable pageable) {
        boolean isUserIdsEmptyOrNull = userIds == null || userIds.isEmpty();

        List<User> users = isUserIdsEmptyOrNull ?
                userRepository.findAll(pageable).toList() :
                userRepository.findAllByIdIn(userIds, pageable);

        return users
                .stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.createUserDtoToUser(createUserDto);

        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    private void checkUser(long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("user", userId));
    }
}
