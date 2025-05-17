package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
@Import({JdbcUserRepository.class})
@DisplayName("JdbcUserRepository")
class JdbcUserRepositoryTest {
    public static final long TEST_USER_ID = 1L;
    private final JdbcUserRepository jdbcUserRepository;

    static User getTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setLogin("user1");
        user.setEmail("test1@mail.com");
        user.setName("user1");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        return user;
    }

    List<User> getAllTestUsers() {
        ArrayList<User> users = new ArrayList<>(3);

        users.add(getTestUser());

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("test2@mail.com");
        user2.setLogin("user2");
        user2.setName("user2");
        user2.setBirthday(LocalDate.of(1998, 1, 1));
        users.add(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setEmail("test3@mail.com");
        user3.setLogin("user3");
        user3.setName("user3");
        user3.setBirthday(LocalDate.of(1997, 1, 1));
        users.add(user3);

        return users;
    }

    private User getTestUserToCreate() {
        User user = new User();

        user.setId(0);
        user.setEmail("test7@mail.com");
        user.setLogin("User7");
        user.setName("User7");
        user.setBirthday(LocalDate.of(2000, 3, 3));

        return user;
    }

    private static User getTestUserToUpdate() {
        User user = new User();

        user.setId(TEST_USER_ID);
        user.setEmail("test8@mail.com");
        user.setLogin("User8");
        user.setName("User8");
        user.setBirthday(LocalDate.of(2002, 5, 5));

        return user;
    }

    @Test
    @DisplayName("Должен находить пользователя по id")
    public void shouldGetUserById() {
        User testUser = getTestUser();
        Optional<User> userOptional = jdbcUserRepository.getUserById(testUser.getId());

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());
    }

    @Test
    @DisplayName("Должен найти информацию о всех пользователях")
    public void shouldGetAllUsers() {
        List<User> usersFromDB = jdbcUserRepository.getAllUsers();
        List<User> usersTest = getAllTestUsers();

        assertThat(usersFromDB).hasSize(usersTest.size());

        for (int i = 0; i < usersFromDB.size(); ++i) {
            assertThat(usersFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(usersTest.get(i));
        }
    }

    @Test
    @DisplayName("Должен создавать нового пользователя")
    void shouldCreateNewUser() {
        User userToCreate = getTestUserToCreate();
        User createdUser = jdbcUserRepository.create(userToCreate);
        Optional<User> userFromDB = jdbcUserRepository.getUserById(createdUser.getId());

        assertThat(userFromDB)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(createdUser);

        assertThat(createdUser)
                .usingRecursiveComparison()
                .ignoringFields("user_id")
                .isEqualTo(userToCreate);
    }

    @Test
    @DisplayName("Должен обновлять пользователя")
    void shouldUpdateUser() {
        User userBeforeUpdate = jdbcUserRepository.getUserById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + TEST_USER_ID));

        assertThat(userBeforeUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());

        jdbcUserRepository.update(getTestUserToUpdate());

        User userAfterUpdate = jdbcUserRepository.getUserById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + TEST_USER_ID));

        assertThat(userAfterUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestUserToUpdate());
    }

    @Test
    @DisplayName("Должен удалять пользователя")
    void shouldDeleteUser() {
        User userBeforeDelete = jdbcUserRepository.getUserById(TEST_USER_ID)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + TEST_USER_ID));

        assertThat(jdbcUserRepository.getAllUsers()).hasSize(3);
        assertThat(userBeforeDelete)
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());

        jdbcUserRepository.deleteUser(TEST_USER_ID);

        assertThat(jdbcUserRepository.getAllUsers()).hasSize(2);
    }

    @Test
    @DisplayName("Должен возвращать друзей пользователя")
    void shouldGetUserFriends() {
        List<User> friendsList = jdbcUserRepository.getUserFriends(TEST_USER_ID);
        List<User> usersTest = getAllTestUsers();

        assertThat(friendsList).hasSize(1);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(1));
    }

    @Test
    @DisplayName("Должен добавлять друга для пользователя")
    void shouldAddFriend() {
        List<User> usersTest = getAllTestUsers();
        jdbcUserRepository.addFriend(3, TEST_USER_ID);

        List<User> friendsList = jdbcUserRepository.getUserFriends(3);

        assertThat(friendsList).hasSize(2);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(0));
    }

    @Test
    @DisplayName("Должен удалять запись о друге")
    void shouldDeleteFriend() {
        jdbcUserRepository.addFriend(TEST_USER_ID, 3);
        List<User> friendsList = jdbcUserRepository.getUserFriends(TEST_USER_ID);
        assertThat(friendsList).hasSize(2);

        jdbcUserRepository.deleteFriend(TEST_USER_ID, 3);

        friendsList = jdbcUserRepository.getUserFriends(TEST_USER_ID);
        assertThat(friendsList).hasSize(1);

        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(getAllTestUsers().get(1));
    }

    @Test
    @DisplayName("Должен возвращать список общих друзей")
    void shouldGetCommonFriends() {
        List<User> friendsList = jdbcUserRepository.getCommonFriends(TEST_USER_ID, 3);

        assertThat(friendsList).hasSize(1);
        assertThat(friendsList.get(0))
                .usingRecursiveComparison()
                .isEqualTo(getAllTestUsers().get(1));
    }

    @Test
    @DisplayName("Должен находить информацию о нескольких пользователях по id")
    void shouldGetFewUsersByIds() {
        List<User> usersFromDB = jdbcUserRepository.getUsersByIds(List.of(1L, 2L));
        List<User> usersTest = getAllTestUsers();

        assertThat(usersFromDB).hasSize(2);

        assertThat(usersFromDB.get(0))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(0));

        assertThat(usersFromDB.get(1))
                .usingRecursiveComparison()
                .isEqualTo(usersTest.get(1));
    }

}