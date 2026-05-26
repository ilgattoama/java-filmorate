package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    private UserController createController() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);

        return new UserController(userService);
    }

    @Test
    void shouldThrowWhenEmailInvalid() {
        User user = new User("invalid-email", "login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginBlank() {
        User user = new User("test@mail.com", " ", "name", LocalDate.of(2000, 1, 1));
        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User("test@mail.com", "my login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User("test@mail.com", "login", "name", LocalDate.now().plusDays(1));
        UserController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameBlank() {
        User user = new User("test@mail.com", "login", " ", LocalDate.of(2000, 1, 1));
        UserController controller = createController();

        User created = controller.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User("test@mail.com", "login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = createController();

        assertDoesNotThrow(() -> controller.create(user));
    }
}