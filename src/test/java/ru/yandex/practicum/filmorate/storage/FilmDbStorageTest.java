package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(FilmDbStorage.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Autowired
    FilmDbStorageTest(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Test
    void shouldCreateAndFindFilmById() {
        Film film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, null));
        film.setGenres(new LinkedHashSet<>(Set.of(new Genre(1, null))));

        Film createdFilm = filmStorage.create(film);

        Film foundFilm = filmStorage.findById(createdFilm.getId());

        assertThat(foundFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(createdFilm);

    }
}