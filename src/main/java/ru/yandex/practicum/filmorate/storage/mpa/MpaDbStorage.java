package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAll() {
        String sql = "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sql, this::mapMpa);
    }

    @Override
    public Mpa findById(Integer id) {
        String sql = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";

        return jdbcTemplate.query(sql, this::mapMpa, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + id + " не найден."));
    }

    private Mpa mapMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}