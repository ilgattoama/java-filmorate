package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Integer id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);

        if (user.getId() == null) {
            throw new ValidationException("Id пользователя не может быть пустым");
        }

        return userStorage.update(user);
    }

    public void addFriend(Integer id, Integer friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId);
        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Integer id, Integer friendId) {
        userStorage.findById(id);
        userStorage.findById(friendId);
        userStorage.removeFriend(id, friendId);
    }

    public Collection<User> getFriends(Integer id) {
        userStorage.findById(id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        userStorage.findById(id);
        userStorage.findById(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}