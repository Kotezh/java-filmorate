package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Activity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<Activity> getActivityById(long activityId);

    Optional<User> getUserById(long userId);

    List<User> getUsersByIds(List<Long> userIds);

    List<User> getAllUsers();

    User create(User user);

    User update(User user);

    void deleteUser(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getUserFriends(long userId);

    List<User> getCommonFriends(long userId, long otherId);
}
