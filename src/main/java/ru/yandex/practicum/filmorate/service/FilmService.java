package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film getFilmById(long filmId);

    List<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    List<Film> getDirectorFilms(long id, String sortBy);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getSearch(String query, String searchBy);
}
