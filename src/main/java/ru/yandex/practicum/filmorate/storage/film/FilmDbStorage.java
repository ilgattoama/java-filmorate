package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id";

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm);
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Film findById(Long id) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";

        Film film = jdbcTemplate.query(sql, this::mapFilm, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));

        loadGenres(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setObject(5, null);
            }

            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(id);

        saveGenres(film);

        return findById(id);
    }

    @Override
    public Film update(Film film) {
        findById(film.getId());

        String sql = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        Integer mpaId = film.getMpa() == null ? null : film.getMpa().getId();

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId()
        );

        deleteGenres(film.getId());
        saveGenres(film);

        return findById(film.getId());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        findById(filmId);

        String sql = "MERGE INTO film_likes (film_id, user_id) " +
                "KEY (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        findById(filmId);

        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> findPopular(Integer count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "m.mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapFilm, count);
        films.forEach(this::loadGenres);
        return films;
    }

    private Film mapFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
        }

        return film;
    }

    private Genre mapGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres AS g " +
                "JOIN film_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";

        Set<Genre> genres = new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapGenre, film.getId()));
        film.setGenres(genres);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "MERGE INTO film_genres (film_id, genre_id) " +
                "KEY (film_id, genre_id) " +
                "VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    private void deleteGenres(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }
}