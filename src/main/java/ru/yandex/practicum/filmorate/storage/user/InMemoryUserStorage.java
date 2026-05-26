package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(nextId.getAndIncrement());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден");
        }

        User oldUser = users.get(user.getId());
        user.setFriends(oldUser.getFriends());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        getById(friendId);

        user.getFriends().add(friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        getById(friendId);

        user.getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = getById(userId);

        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getById(userId);
        User otherUser = getById(otherId);

        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public User add(User user) {
        return create(user);
    }

    public User getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }

        users.remove(id);
    }
}