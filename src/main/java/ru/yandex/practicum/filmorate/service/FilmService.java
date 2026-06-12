package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) {
        validateFilm(film);
        validateMpaAndGenres(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);
        validateMpaAndGenres(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(Math.toIntExact(userId));
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(Math.toIntExact(userId));
        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> findPopular(Integer count) {
        return filmStorage.findPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов.");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Mpa mpa = mpaService.findById(film.getMpa().getId());
            film.setMpa(mpa);
        }

        if (film.getGenres() != null) {
            Set<Genre> checkedGenres = new LinkedHashSet<>();

            for (Genre genre : film.getGenres()) {
                if (genre == null || genre.getId() == null) {
                    throw new NotFoundException("Жанр с id null не найден.");
                }

                Genre checkedGenre = genreService.findById(genre.getId());
                checkedGenres.add(checkedGenre);
            }

            film.setGenres(checkedGenres);
        }
    }
}