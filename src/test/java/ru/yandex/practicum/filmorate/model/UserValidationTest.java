package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class UserValidationTest {

    private UserController createController() {
        UserStorage userStorage = mock(UserStorage.class);
        UserService userService = new UserService(userStorage);

        return new UserController(userService);
    }

    @Test
    void shouldThrowWhenEmailInvalid() {
        User user = new User(
                null,
                "invalid-email",
                "login",
                "name",
                LocalDate.of(2000, 1, 1)
        );

        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenEmailBlank() {
        User user = new User(
                null,
                "",
                "login",
                "name",
                LocalDate.of(2000, 1, 1)
        );

        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginBlank() {
        User user = new User(
                null,
                "mail@mail.ru",
                "",
                "name",
                LocalDate.of(2000, 1, 1)
        );

        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User(
                null,
                "mail@mail.ru",
                "lo gin",
                "name",
                LocalDate.of(2000, 1, 1)
        );

        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User(
                null,
                "mail@mail.ru",
                "login",
                "name",
                LocalDate.now().plusDays(1)
        );

        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldNotThrowWhenUserValid() {
        User user = new User(
                null,
                "mail@mail.ru",
                "login",
                "name",
                LocalDate.of(2000, 1, 1)
        );

        UserController controller = createController();

        assertDoesNotThrow(() -> controller.create(user));
    }

    @Test
    void shouldUseLoginWhenNameBlank() {
        User user = new User(
                null,
                "mail@mail.ru",
                "login",
                "",
                LocalDate.of(2000, 1, 1)
        );

        assertDoesNotThrow(() -> createController().create(user));
    }
}