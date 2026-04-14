package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        FilmController controller = new FilmController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        FilmController controller = new FilmController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        FilmController controller = new FilmController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDurationNotPositive() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        FilmController controller = new FilmController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        FilmController controller = new FilmController();

        assertDoesNotThrow(() -> controller.create(film));
    }
}