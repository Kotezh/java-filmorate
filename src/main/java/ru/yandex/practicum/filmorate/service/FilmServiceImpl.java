package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.dal.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.dal.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class FilmServiceImpl implements FilmService {
    private final JdbcFilmRepository jdbcFilmRepository;
    private final JdbcUserRepository jdbcUserRepository;
    private final JdbcGenreRepository jdbcGenreRepository;
    private final JdbcMpaRepository jdbcMpaRepository;

    @Override
    public Film getFilmById(long filmId) {
        return jdbcFilmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
    }

    @Override
    public List<Film> getFilms() {
        List<Film> allFilms = jdbcFilmRepository.getAllFilms();
        return allFilms;
    }

    @Override
    public Film create(Film film) {
        film.setLikesCount(0);
        Mpa mpa = jdbcMpaRepository.getMpaById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + film.getMpa().getId() + " не найден"));
        film.setMpa(mpa);
        if (film.getGenres() != null) {
            List<Genre> genres = jdbcGenreRepository.getGenresByIds(film.getGenres().stream()
                    .map(Genre::getId)
                    .toList());
            if (genres.size() != film.getGenres().size()) {
                genres.forEach(film.getGenres()::remove);
                throw new NotFoundException("Жанры не найдены: " +
                        film.getGenres().stream()
                                .map(Genre::getId)
                                .toList());
            }
            film.setGenres(new LinkedHashSet<>(genres));
        }
        log.info("Создан новый фильм");
        return jdbcFilmRepository.create(film);
    }

    @Override
    public Film update(Film film) {

        if (film.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (jdbcFilmRepository.getAllFilms().stream().anyMatch((oldFilm) -> oldFilm.getId() == film.getId())) {
            Film savedFilm = getFilmById(film.getId());
            film.setLikesCount(savedFilm.getLikesCount());
            Mpa mpa = jdbcMpaRepository.getMpaById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id " + film.getMpa().getId() + " не найден"));
            film.setMpa(mpa);
            if (film.getGenres() == null) {
                film.setGenres(savedFilm.getGenres());
            } else {
                List<Genre> genres = jdbcGenreRepository.getGenresByIds(film.getGenres().stream()
                        .map(Genre::getId)
                        .toList());
                if (genres.size() != film.getGenres().size()) {
                    genres.forEach(film.getGenres()::remove);
                    throw new NotFoundException("Жанры не найдены: " +
                            film.getGenres().stream()
                                    .map(Genre::getId)
                                    .toList());
                }
            }
            log.info("Фильм обновлён");
            return jdbcFilmRepository.update(film);
        }
        log.trace("Фильм с id = " + film.getId() + " не найден");
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public void deleteFilm(long filmId) {
        getFilmById(filmId);
        jdbcFilmRepository.deleteFilm(filmId);
        log.info("Фильм удален");
    }

    @Override
    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        jdbcFilmRepository.addLike(filmId, userId);
        film.setLikesCount(film.getLikesCount() + 1);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        jdbcUserRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        jdbcFilmRepository.deleteLike(filmId, userId);
        film.setLikesCount(film.getLikesCount() - 1);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = jdbcFilmRepository.getPopularFilms(count);
        return popularFilms;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId){
        List<Film> commonFilms = jdbcFilmRepository.getCommonFilms(userId, friendId);
        return commonFilms;
    }
}
