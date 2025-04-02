package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User get(long userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> allUsers = users.values();
        return allUsers;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);
    }

    @Override
    public Set<User> findAllFriends(long userId) {
        User user = users.get(userId);
        Collection<Long> allFriendsIds = user.getFriends();
        Set<User> allFriends = allFriendsIds.stream().map(users::get).collect(Collectors.toSet());

        return allFriends;
    }

    @Override
    public Collection<User> findCommonFriends(long userId, long otherId) {
        Collection<User> allUserFriends = findAllFriends(userId);
        Collection<User> allOtherFriends = findAllFriends(otherId);

        Set<User> allCommonFriends = allUserFriends.stream().filter(allOtherFriends::contains).collect(Collectors.toSet());
        return allCommonFriends;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
