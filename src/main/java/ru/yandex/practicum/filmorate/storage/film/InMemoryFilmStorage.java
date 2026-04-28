package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film add(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        films.remove(id);
    }
}