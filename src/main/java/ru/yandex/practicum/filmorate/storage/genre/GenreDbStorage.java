package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAll() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, this::mapGenre);
    }

    @Override
    public Genre findById(Integer id) {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";

        return jdbcTemplate.query(sql, this::mapGenre, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден."));
    }

    private Genre mapGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}