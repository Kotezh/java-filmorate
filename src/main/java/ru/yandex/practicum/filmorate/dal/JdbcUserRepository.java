package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<User> mapper;

    private static final String CREATE_USER_QUERY = "INSERT INTO users (login, email, name, birthday) VALUES(:login,:email,:name,:birthday)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET login=:login, email=:email, name=:name, birthday=:birthday WHERE user_id=:user_id";
    private static final String GET_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :user_id";
    private static final String GET_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends(user_id, friend_id) VALUES(:user_id,:friend_id)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id=:user_id AND friend_id=:friend_id";
    private static final String GET_USER_FRIENDS_QUERY = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = :user_id)";
    private static final String FIND_COMMON_FRIENDS = "SELECT u.* FROM users u JOIN friends f1 ON u.user_id = f1.friend_id "
            + "JOIN friends f2 ON u.user_id = f2.friend_id WHERE f1.user_id = :user_id AND f2.user_id = :other_id";
    private static final String FIND_USERS_BY_IDS_QUERY = "SELECT * FROM users WHERE user_id IN (:ids)";

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", user.getLogin());
        params.addValue("email", user.getEmail());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        jdbc.update(CREATE_USER_QUERY, params, keyHolder);
        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    @Override
    public User update(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", user.getLogin());
        params.addValue("email", user.getEmail());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        params.addValue("user_id", user.getId());
        jdbc.update(UPDATE_USER_QUERY, params);
        return user;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        try (Stream<User> stream = jdbc.queryForStream(GET_USER_BY_ID_QUERY, params, mapper)) {
            return stream.findAny();
        }
    }

    @Override
    public List<User> getAllUsers() {
        return jdbc.query(GET_ALL_USERS_QUERY, mapper);

    }

    @Override
    public void addFriend(long userId, long friendId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);
        jdbc.update(ADD_FRIEND_QUERY, params, keyHolder);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);
        jdbc.update(DELETE_FRIEND_QUERY, params, keyHolder);
    }

    @Override
    public List<User> getUserFriends(long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        return jdbc.query(GET_USER_FRIENDS_QUERY, params, mapper);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("other_id", otherId);
        return jdbc.query(FIND_COMMON_FRIENDS, params, mapper);
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        return jdbc.query(FIND_USERS_BY_IDS_QUERY, params, mapper);
    }
}