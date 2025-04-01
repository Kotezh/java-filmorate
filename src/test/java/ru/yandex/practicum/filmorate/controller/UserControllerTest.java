package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;
    UserController userController;

    @BeforeEach
    void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userController = new UserController(userService);
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
    void shouldBeCreatedValidUser() {
        User savedUser = createTestUser1();

        assertEquals("cat@mail.ru", savedUser.getEmail());
        assertEquals("cat", savedUser.getLogin());
        assertEquals("Cat", savedUser.getName());
        assertEquals(LocalDate.of(2000, 12, 12), savedUser.getBirthday());

        savedUser.setEmail("111111");
        assertThrows(NotFoundException.class, () -> userController.update(savedUser));

        savedUser.setEmail("cat.com");
        assertThrows(NotFoundException.class, () -> userController.update(savedUser));

        savedUser.setEmail("cat5@mail.ru");
        savedUser.setLogin("");
        assertThrows(NotFoundException.class, () -> userController.update(savedUser));

        savedUser.setLogin(null);
        assertThrows(NotFoundException.class, () -> userController.update(savedUser));


        savedUser.setBirthday(LocalDate.now().plusDays(2));
        assertThrows(NotFoundException.class, () -> userController.update(savedUser));
    }

    @Test
    void shouldBeUpdatedUser() throws NotFoundException, ValidationException {
        User user = createTestUser2();
        user.setName(null);
        userController.create(user);

        user.setName("newName");
        userController.update(user);
        Collection<User> users = userController.findAll();
        assertEquals(3, users.size(), "Количество пользователей не совпадает");
        assertEquals("newName", userService.getUser(3).getName(), "Логин пользователя не совпадает");
    }
}


