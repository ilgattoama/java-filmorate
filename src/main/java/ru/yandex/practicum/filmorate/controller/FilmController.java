package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public List<Film> findAll() {
        return films;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(nextId++);
        films.add(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);

        if (film.getId() == null) {
            log.error("Ошибка обновления фильма: id не указан");
            throw new ValidationException("Id фильма должен быть указан");
        }

        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId().equals(film.getId())) {
                films.set(i, film);
                log.info("Обновлён фильм: {}", film);
                return film;
            }
        }

        log.error("Ошибка обновления фильма: фильм с id={} не найден", film.getId());
        throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка валидации фильма: пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации фильма: описание длиннее 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(minReleaseDate)) {
            log.error("Ошибка валидации фильма: некорректная дата релиза");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.error("Ошибка валидации фильма: продолжительность <= 0");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}