package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getFilmById(long filmId);

    List<Film> getAllFilms();

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(long filmId);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    void setFilmGenres(Film film);

    LinkedHashSet<Genre> getFilmGenres(Film film);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);
}
