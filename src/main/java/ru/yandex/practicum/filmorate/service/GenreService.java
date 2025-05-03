package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre getGenreById(long id);

    List<Genre> getAllGenres();

    void load(List<Film> films);
}
