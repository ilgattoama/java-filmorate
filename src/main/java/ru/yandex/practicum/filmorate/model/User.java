package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Objects;

public class User {
    private Integer id;
    private final String email;
    private final String login;
    private final String name;
    private final LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public User withName(String newName) {
        User user = new User(this.email, this.login, newName, this.birthday);
        user.setId(this.id);
        return user;
    }

    public User withId(Integer newId) {
        User user = new User(this.email, this.login, this.name, this.birthday);
        user.setId(newId);
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return Objects.equals(id, user.id)
                && Objects.equals(email, user.email)
                && Objects.equals(login, user.login)
                && Objects.equals(name, user.name)
                && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday);
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", email='" + email + '\''
                + ", login='" + login + '\''
                + ", name='" + name + '\''
                + ", birthday=" + birthday
                + '}';
    }
}