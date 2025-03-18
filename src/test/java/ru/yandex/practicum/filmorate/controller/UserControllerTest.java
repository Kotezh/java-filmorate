package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    private User createTestUser1() {
        User user = new User();
        user.setLogin("cat");
        user.setEmail("cat@mail.ru");
        user.setName("Cat");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        return user;
    }

    private User createTestUser2() {
        User user = new User();
        user.setLogin("dog");
        user.setEmail("dog@mail.ru");
        user.setName("Dog");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    public void shouldBeCreatedTwoUsers() throws ValidationException {
        User user1 = createTestUser1();
        User user2 = createTestUser2();
        userController.create(user1);
        userController.create(user2);

        Collection<User> users = userController.findAll();
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        assertEquals(2, users.size(), "Количество пользователей не совпадает");
        assertEquals("Cat", users.stream().findFirst().get().getName(), "Имя пользователя не совпадает");
    }

    @Test
    void shouldBeCreatedValidUser() throws ValidationException {
        User user = createTestUser1();
        User savedUser = userController.create(user);

        assertEquals("cat@mail.ru", savedUser.getEmail());
        assertEquals("cat", savedUser.getLogin());
        assertEquals("Cat", savedUser.getName());
        assertEquals(LocalDate.of(2000, 12, 12), savedUser.getBirthday());

        user.setEmail("111111");
        assertThrows(ValidationException.class, () -> userController.create(user));

        user.setEmail("cat.com");
        assertThrows(ValidationException.class, () -> userController.create(user));

        user.setEmail("cat@mail.ru");
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.create(user));

        user.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.create(user));

        user.setLogin("cat cat");
        assertThrows(ValidationException.class, () -> userController.create(user));

        user.setBirthday(LocalDate.now().plusDays(2));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldBeUpdatedUser() throws ValidationException {
        User user = createTestUser1();
        user.setName(null);
        userController.create(user);

        User updatedUser = createTestUser2();
        updatedUser.setEmail("cat@mail.ru");
        updatedUser.setLogin("cat");
        updatedUser.setName(null);
        updatedUser.setBirthday(LocalDate.of(1965, 3, 3));
        updatedUser.setId(1);
        assertThrows(ValidationException.class, () -> userController.update(updatedUser));
    }
}


