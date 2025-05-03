package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User getUserById(long userId);

    List<User> getUsers();

    User create(@Valid User user);

    User update(@Valid User user);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getAllFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
