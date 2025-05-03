
MERGE INTO genres (genre_id, genre_name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, genre_name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, genre_name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, genre_name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, genre_name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, genre_name) VALUES (6, 'Боевик');

MERGE INTO mpa (mpa_id, mpa_name) VALUES (1, 'G');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (2, 'PG');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (3, 'PG-13');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (4, 'R');
MERGE INTO mpa (mpa_id, mpa_name) VALUES (5, 'NC-17');

MERGE INTO users (user_id, login, email, name, birthday) VALUES('1', 'user1', 'test1@mail.com', 'user1', '1999-01-01');
MERGE INTO users (user_id, login, email, name, birthday) VALUES('2', 'user2', 'test2@mail.com', 'user2', '1998-01-01');
MERGE INTO users (user_id, login, email, name, birthday) VALUES('3', 'user3', 'test3@mail.com', 'user3', '1997-01-01');

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 4;

MERGE INTO friends (user_id, friend_id) VALUES (1, 2);
MERGE INTO friends (user_id, friend_id) VALUES (3, 2);

MERGE INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(
    1,
    'Фильм 1',
    'Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1',
    '2000-05-05',
    127,
    3
);
MERGE INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(
    2,
    'Фильм 2',
    'Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2',
    '1950-12-12',
    138,
    2
);
MERGE INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES
(
    3,
    'Фильм 3',
    'Описание фильма 3 Описание фильма 3 Описание фильма 3',
    '2023-11-11',
    220,
    2
);

ALTER TABLE films ALTER COLUMN film_id RESTART WITH 4;

MERGE INTO film_genres (genre_id, film_id) VALUES (1, 1);
MERGE INTO film_genres (genre_id, film_id) VALUES (1, 2);
MERGE INTO film_genres (genre_id, film_id) VALUES (1, 3);
MERGE INTO film_genres (genre_id, film_id) VALUES (3, 3);

MERGE INTO likes (film_id, user_id) VALUES (1, 1);
MERGE INTO likes (film_id, user_id) VALUES (2, 1);
MERGE INTO likes (film_id, user_id) VALUES (2, 2);
MERGE INTO likes (film_id, user_id) VALUES (2, 3);
MERGE INTO likes (film_id, user_id) VALUES (3, 2);
MERGE INTO likes (film_id, user_id) VALUES (3, 3);