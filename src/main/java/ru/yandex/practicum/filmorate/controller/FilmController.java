package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findPopular(count);
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
}