package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTest {

    @Test
    void shouldThrowWhenEmailInvalid() {
        User user = new User("invalid-email", "login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginBlank() {
        User user = new User("test@mail.com", " ", "name", LocalDate.of(2000, 1, 1));
        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User("test@mail.com", "my login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User("test@mail.com", "login", "name", LocalDate.now().plusDays(1));
        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameBlank() {
        User user = new User("test@mail.com", "login", " ", LocalDate.of(2000, 1, 1));
        UserController controller = new UserController();
        User created = controller.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User("test@mail.com", "login", "name", LocalDate.of(2000, 1, 1));
        UserController controller = new UserController();

        assertDoesNotThrow(() -> controller.create(user));
    }
}