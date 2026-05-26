package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "ORDER BY f.film_id";

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm);
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        loadGenres(film);
        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            if (film.getMpa() == null || film.getMpa().getId() == null) {
                statement.setObject(5, null);
            } else {
                statement.setInt(5, film.getMpa().getId());
            }

            return statement;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        saveGenres(film);

        return findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + film.getId() + " не найден"));
    }

    @Override
    public Film update(Film film) {
        if (findById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        Integer mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId,
                film.getId()
        );

        deleteGenres(film.getId());
        saveGenres(film);

        return findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + film.getId() + " не найден"));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        String sql = "MERGE INTO film_likes KEY(film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(fl.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name " +
                "ORDER BY likes_count DESC, f.film_id " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm, count);
        films.forEach(this::loadGenres);
        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Integer> uniqueGenreIds = new LinkedHashSet<>();

        for (Genre genre : film.getGenres()) {
            if (genre != null && genre.getId() != null) {
                uniqueGenreIds.add(genre.getId());
            }
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Integer genreId : uniqueGenreIds) {
            jdbcTemplate.update(sql, film.getId(), genreId);
        }
    }

    private void deleteGenres(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        Set<Genre> genres = new LinkedHashSet<>(
                jdbcTemplate.query(sql, this::mapGenre, film.getId())
        );

        film.setGenres(genres);
    }

    private void checkFilmExists(Long filmId) {
        if (findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
    }

    private void checkUserExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        if (count == null || count == 0) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private Film mapFilm(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) {
            film.setMpa(new Mpa(mpaId, rs.getString("mpa_name")));
        }

        film.setGenres(new LinkedHashSet<>());

        return film;
    }

    private Genre mapGenre(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }
}