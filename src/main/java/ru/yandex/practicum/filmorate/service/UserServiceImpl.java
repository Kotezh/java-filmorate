package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Activity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    private final JdbcUserRepository jdbcUserRepository;

    @Override
    public List<Activity> getActivityById(long userId) {
        List<Activity> activities = jdbcUserRepository.getActivityById(userId);
        if (activities.isEmpty()) {
            throw new NotFoundException("Активность пользователя не найдена");
        }
        return activities;
    }

    @Override
    public User getUserById(long userId) {
        return jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    @Override
    public List<User> getUsers() {
        return jdbcUserRepository.getAllUsers();
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("Создан новый пользователь");
        return jdbcUserRepository.create(user);
    }

    @Override
    public User update(User user) {
        if (user.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (jdbcUserRepository.getAllUsers().stream().anyMatch((oldUser) -> oldUser.getId() == user.getId())) {
            log.info("Пользователь обновлён");
            return jdbcUserRepository.update(user);
        }
        log.trace("Пользователь с id = " + user.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public void deleteUser(long userId) {
        getUserById(userId);
        jdbcUserRepository.deleteUser(userId);
        log.info("Пользователь удален");
    }

    private void checkUsersInTable(List<Long> usersIds) {
        List<User> foundUsers = jdbcUserRepository.getUsersByIds(usersIds);
        if (foundUsers.size() != usersIds.size()) {
            usersIds.removeAll(foundUsers.stream()
                    .map(User::getId)
                    .toList());
            throw new NotFoundException("Пользователь с id = " + usersIds + " не найден");
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        checkUsersInTable(new ArrayList<>(List.of(userId, friendId)));
        jdbcUserRepository.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        checkUsersInTable(new ArrayList<>(List.of(userId, friendId)));
        jdbcUserRepository.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        getUserById(userId);
        return jdbcUserRepository.getUserFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        checkUsersInTable(new ArrayList<>(List.of(userId, otherId)));

        return jdbcUserRepository.getCommonFriends(userId, otherId);
    }
}
