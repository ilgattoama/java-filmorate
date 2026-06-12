package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Integer id);

    User create(User user);

    User update(User user);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    Collection<User> getFriends(Integer userId);

    Collection<User> getCommonFriends(Integer userId, Integer otherId);
}