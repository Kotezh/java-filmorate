package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    User get(long userId);

    Collection<User> getUsers();

    User create(User user);

    User update(User user);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Set<User> findAllFriends(long userId);

    Collection<User> findCommonFriends(long userId, long otherId);
}
