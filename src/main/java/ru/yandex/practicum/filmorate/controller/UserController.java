package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public List<User> findAll() {
        return users;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(nextId++);
        users.add(user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);

        if (user.getId() == null) {
            log.error("Ошибка обновления пользователя: id не указан");
            throw new ValidationException("Id пользователя должен быть указан");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                log.info("Обновлён пользователь: {}", user);
                return user;
            }
        }

        log.error("Ошибка обновления пользователя: пользователь с id={} не найден", user.getId());
        throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации пользователя: некорректный email");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации пользователя: некорректный login");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации пользователя: дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}