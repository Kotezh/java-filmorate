package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    Optional<Film> getFilmById(long filmId);

    List<Film> getAllFilms();

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(long filmId);

    LinkedHashSet<Director> getFilmDirectors(Film film);

    void connectGenres(Collection<Film> films);

    void connectDirectors(Collection<Film> films);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    void setFilmGenres(Film film);

    void setFilmDirectors(Film film);

    LinkedHashSet<Genre> getFilmGenres(Film film);

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    List<Film> getDirectorFilmsByYear(long id);

    List<Film> getDirectorFilmsByLikes(long id);

    List<Film> getCommonFilms(long userId, long friendId);
}
