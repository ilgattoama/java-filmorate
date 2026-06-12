package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getInt("user_id"))
            .email(rs.getString("email"))
            .login(rs.getString("login"))
            .name(rs.getString("name"))
            .birthday(rs.getDate("birthday").toLocalDate())
            .build();

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT user_id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT user_id, email, login, name, birthday FROM users WHERE user_id = ?";

        return jdbcTemplate.query(sql, userRowMapper, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";

        int updatedRows = jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (updatedRows == 0) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        return findById(user.getId());
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sql = "MERGE INTO friendships (user_id, friend_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendships " +
                "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "JOIN friendships AS f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        String sql = "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
                "FROM users AS u " +
                "JOIN friendships AS f1 ON u.user_id = f1.friend_id " +
                "JOIN friendships AS f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}