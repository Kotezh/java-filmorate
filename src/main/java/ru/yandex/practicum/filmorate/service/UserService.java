package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserStorage userStorage;

    public User getUser(long userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getUsers();
    }

    public User create(@Valid User user) {
        log.info("Создан новый пользователь");
        return userStorage.create(user);
    }

    public User update(@Valid User user) {
        if (user.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.getUsers().stream().anyMatch((oldUser) -> oldUser.getId() == user.getId())) {
            log.info("Пользователь обновлён");
            return userStorage.update(user);
        }
        log.trace("Пользователь с id = " + user.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    public void addFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> findAllFriends(long userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.findAllFriends(userId);
    }

    public Collection<User> findCommonFriends(long userId, long otherId) {
        User user = getUser(userId);
        User other = getUser(otherId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (other == null) {
            throw new NotFoundException("Друг с id = " + otherId + " не найден");
        }

        return userStorage.findCommonFriends(userId, otherId);
    }
}
