package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    Optional<Genre> getGenreById(long id);

    List<Genre> getAllGenres();

    List<Genre> getGenresByIds(List<Long> ids);

    List<Film> load(List<Film> films);
}
