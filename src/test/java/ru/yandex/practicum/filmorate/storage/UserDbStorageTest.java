package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserDbStorage.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Autowired
    UserDbStorageTest(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    void shouldCreateAndFindUserById() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userStorage.create(user);

        User foundUser = userStorage.findById(createdUser.getId());

        assertThat(foundUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(createdUser);
    }
}