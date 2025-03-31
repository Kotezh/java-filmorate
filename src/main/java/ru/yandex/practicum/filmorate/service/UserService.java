package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User getUser(long userId) throws NotFoundException {
        User user = userStorage.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user;
    }

    public Collection<User> findAll() {
        return userStorage.getUsers();
    }

    public User create(User user) throws ValidationException {
        validateUser(user);
        log.info("Создан новый пользователь");
        return userStorage.create(user);
    }

    public User update(User user) throws NotFoundException, ValidationException {
        if (user.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (userStorage.getUsers().stream().anyMatch((oldUser) -> oldUser.getId() == user.getId())) {
            validateUser(user);

            log.info("Пользователь обновлён");
            return userStorage.update(user);
        }
        log.trace("Пользователь с id = " + user.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    public void addFriend(long userId, long friendId) throws NotFoundException {
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

    public void deleteFriend(long userId, long friendId) throws NotFoundException {
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

    public Collection<User> findAllFriends(long userId) throws NotFoundException {
        User user = getUser(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return userStorage.findAllFriends(userId);
    }

    public Collection<User> findCommonFriends(long userId, long otherId) throws NotFoundException {
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

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null) {
            log.info("Имя отсутствует и заменено на логин");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
