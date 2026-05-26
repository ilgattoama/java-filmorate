package ru.yandex.practicum.filmorate.storage.Mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, this::mapMpa);
    }

    @Override
    public Optional<Mpa> findById(Integer id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        List<Mpa> ratings = jdbcTemplate.query(sql, this::mapMpa, id);
        return ratings.stream().findFirst();
    }

    private Mpa mapMpa(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name")
        );
    }
}