package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    //    private static Long id = 0L;

    @Override
    public Film get(long filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> allFilms = films.values();
        return allFilms;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setReleaseDate(film.getReleaseDate());
        return oldFilm;
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.addLike(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = films.get(filmId);
        film.deleteLike(userId);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
