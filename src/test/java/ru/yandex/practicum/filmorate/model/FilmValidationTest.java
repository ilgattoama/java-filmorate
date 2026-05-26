package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTest {

    private FilmController createController() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage);

        return new FilmController(filmService);
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        Film film = new Film(" ", "Description", LocalDate.of(2000, 1, 1), 100);
        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film("Film", "A".repeat(201), LocalDate.of(2000, 1, 1), 100);
        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenReleaseDateTooEarly() {
        Film film = new Film("Film", "Description", LocalDate.of(1895, 12, 27), 100);
        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDurationNotPositive() {
        Film film = new Film("Film", "Description", LocalDate.of(2000, 1, 1), 0);
        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film("Film", "Description", LocalDate.of(2000, 1, 1), 120);
        FilmController controller = createController();

        assertDoesNotThrow(() -> controller.create(film));
    }
}