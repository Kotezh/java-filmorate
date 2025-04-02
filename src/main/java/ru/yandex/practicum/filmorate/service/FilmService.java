package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilm(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return film;
    }

    public Collection<Film> findAll() {
        Collection<Film> allFilms = filmStorage.getFilms();
        return allFilms;
    }

    public Film create(@Valid Film film) {
        log.info("Создан новый фильм");
        return filmStorage.create(film);
    }

    public Film update(@Valid Film film) {

        if (film.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (filmStorage.getFilms().stream().anyMatch((oldFilm) -> oldFilm.getId() == film.getId())) {
            log.info("Фильм обновлён");
            return filmStorage.update(film);
        }
        log.trace("Фильм с id = " + film.getId() + " не найден");
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public void addLike(long filmId, long userId) {
        User user = userStorage.get(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        User user = userStorage.get(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        if (getFilm(filmId) == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> findPopularFilms(int count) {
        Collection<Film> popularFilms = filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
        return popularFilms;
    }
}
