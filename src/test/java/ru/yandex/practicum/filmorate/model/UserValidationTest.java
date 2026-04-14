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
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginBlank() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin(" ");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("my login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now().plusDays(1));

        UserController controller = new UserController();

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameBlank() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName(" ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();
        User created = controller.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        UserController controller = new UserController();

        assertDoesNotThrow(() -> controller.create(user));
    }
}