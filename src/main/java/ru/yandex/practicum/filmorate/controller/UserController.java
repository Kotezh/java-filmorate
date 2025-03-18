package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        // проверяем выполнение необходимых условий
        validateUser(newUser);
        // формируем дополнительные данные
        newUser.setId(getNextId());
        // сохраняем нового юзера в памяти приложения
        users.put(newUser.getId(), newUser);
        log.info("Создан новый пользователь");
        return newUser;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User newUser) {

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (newUser.getName() == null) {
            log.info("Имя отсутствует и заменено на логин");
            newUser.setName(newUser.getLogin());
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail().toLowerCase().equals(newUser.getEmail()))) {
            log.error("Этот имейл уже используется");
            throw new ValidationException("Этот имейл уже используется");
        }
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            validateUser(newUser);
            // если пользователь найден и все условия соблюдены, обновляем его содержимое
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь обновлён");
            return oldUser;
        }
        log.trace("Пользователь с id = " + newUser.getId() + " не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}
