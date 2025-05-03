package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film getById(long filmId);

    List<Film> getFilms();

    Film create(@Valid Film film);

    Film update(@Valid Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getPopularFilms(int count);
}
