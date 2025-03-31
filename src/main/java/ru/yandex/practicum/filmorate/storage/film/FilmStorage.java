package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film get(long filmId);

    Collection<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}
