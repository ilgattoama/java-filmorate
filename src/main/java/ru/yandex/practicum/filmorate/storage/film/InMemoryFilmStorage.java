package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId.getAndIncrement());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }

        Film oldFilm = films.get(film.getId());
        film.setLikes(oldFilm.getLikes());
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        int limit = count == null ? 10 : count;

        return films.values().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Film add(Film film) {
        return create(film);
    }

    public Film getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }

        films.remove(id);
    }
}