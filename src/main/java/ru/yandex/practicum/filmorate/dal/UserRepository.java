package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
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

    Set<Long> getUserLikedFilms(long userId);

    Map<Long, Set<Long>> getAllUserLikes();
}
