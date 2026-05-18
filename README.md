# java-filmorate

## ER-диаграмма
<img width="1236" height="738" alt="filmorate-er-diagram" src="https://github.com/user-attachments/assets/509ec92b-ee95-4fec-99f2-f4127f46df2a" />

## Описание схемы

База данных хранит пользователей, фильмы, жанры, рейтинги MPA, лайки и дружеские связи между пользователями.

Таблица `users` хранит пользователей приложения.

Таблица `films` хранит фильмы. У фильма может быть один рейтинг MPA.

Таблица `mpa_ratings` хранит справочник рейтингов MPA: G, PG, PG-13, R, NC-17.

Таблица `genres` хранит справочник жанров.

Таблица `film_genres` связывает фильмы и жанры, потому что у одного фильма может быть несколько жанров.

Таблица `film_likes` хранит лайки пользователей к фильмам.

Таблица `friendships` хранит дружбу между пользователями.

Таблица `friendship_statuses` хранит статусы дружбы: неподтверждённая и подтверждённая.

## Примеры SQL-запросов

### Получить все фильмы

```sql
SELECT f.film_id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       m.name AS mpa
FROM films AS f
LEFT JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_id;
```

### Получить все фильмы с жанрами

```sql
SELECT f.film_id,
       f.name,
       g.name AS genre
FROM films AS f
LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
LEFT JOIN genres AS g ON fg.genre_id = g.genre_id;
```

### Получить топ-10 популярных фильмов

```sql
SELECT f.film_id,
       f.name,
       COUNT(fl.user_id) AS likes_count
FROM films AS f
LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;
```

### Получить друзей пользователя

```sql
SELECT u.user_id,
       u.login,
       u.name
FROM friendships AS fr
JOIN users AS u ON fr.friend_id = u.user_id
JOIN friendship_statuses AS fs ON fr.status_id = fs.status_id
WHERE fr.user_id = 1
  AND fs.name = 'CONFIRMED';
```

### Получить общих друзей двух пользователей

```sql
SELECT u.user_id,
       u.login,
       u.name
FROM friendships AS f1
JOIN friendships AS f2 ON f1.friend_id = f2.friend_id
JOIN users AS u ON f1.friend_id = u.user_id
JOIN friendship_statuses AS fs1 ON f1.status_id = fs1.status_id
JOIN friendship_statuses AS fs2 ON f2.status_id = fs2.status_id
WHERE f1.user_id = 1
  AND f2.user_id = 2
  AND fs1.name = 'CONFIRMED'
  AND fs2.name = 'CONFIRMED';
```
