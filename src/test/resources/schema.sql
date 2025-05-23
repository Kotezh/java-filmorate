CREATE TABLE IF NOT EXISTS genres (
    genre_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name      VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name     VARCHAR(200) NOT NULL UNIQUE,
    PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS films (
    film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    description  VARCHAR(200),
    release_date DATE CHECK (release_date <= CURRENT_DATE),
    duration     INT CHECK (duration > 0),
    mpa_id       BIGINT REFERENCES mpa(mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id   BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id  BIGINT NOT NULL REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email        VARCHAR(200) NOT NULL UNIQUE,
    login        VARCHAR(200)  NOT NULL UNIQUE,
    name         VARCHAR(200),
    birthday     DATE CHECK (birthday <= CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id     BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id   BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id     BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);
