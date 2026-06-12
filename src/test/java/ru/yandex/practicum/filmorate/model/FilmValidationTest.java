package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class FilmValidationTest {

    private FilmController createController() {
        FilmStorage filmStorage = mock(FilmStorage.class);
        UserStorage userStorage = mock(UserStorage.class);
        MpaService mpaService = mock(MpaService.class);
        GenreService genreService = mock(GenreService.class);

        FilmService filmService = new FilmService(
                filmStorage,
                userStorage,
                mpaService,
                genreService
        );

        return new FilmController(filmService);
    }

    @Test
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film(
                null,
                "Film name",
                "a".repeat(201),
                LocalDate.of(2000, 1, 1),
                100
        );

        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenReleaseDateBeforeMinimumDate() {
        Film film = new Film(
                null,
                "Film name",
                "Description",
                LocalDate.of(1895, 12, 27),
                100
        );

        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDurationIsNegative() {
        Film film = new Film(
                null,
                "Film name",
                "Description",
                LocalDate.of(2000, 1, 1),
                -100
        );

        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldThrowWhenDurationIsZero() {
        Film film = new Film(
                null,
                "Film name",
                "Description",
                LocalDate.of(2000, 1, 1),
                0
        );

        FilmController controller = createController();

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotThrowWhenFilmValid() {
        Film film = new Film(
                null,
                "Film name",
                "Description",
                LocalDate.of(2000, 1, 1),
                100
        );

        FilmController controller = createController();

        assertDoesNotThrow(() -> controller.create(film));
    }

    @Test
    void shouldNotThrowWhenReleaseDateEqualsMinimumDate() {
        Film film = new Film(
                null,
                "Film name",
                "Description",
                LocalDate.of(1895, 12, 28),
                100
        );

        FilmController controller = createController();

        assertDoesNotThrow(() -> controller.create(film));
    }
}