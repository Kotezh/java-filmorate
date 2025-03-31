package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;
    FilmService filmService;
    FilmController filmController;

    @BeforeEach
    void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
        filmController = new FilmController(filmService);
    }

    private Film createTestFilm1() {
        Film film = new Film();
        film.setName("Movie1");
        film.setDescription("Movie1 description");
        film.setReleaseDate(LocalDate.of(1975, 1, 1));
        film.setDuration(2);
        return film;
    }

    private Film createTestFilm2() {
        Film film = new Film();
        film.setName("Movie2");
        film.setDescription("Movie2 description");
        film.setReleaseDate(LocalDate.of(1999, 7, 10));
        film.setDuration(3);
        return film;
    }

    @Test
    void shouldBeAddedOneFilm() {
        Film film = createTestFilm1();
        filmController.create(film);

        Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Количество фильмов не совпадает");
        assertEquals("Movie1", films.stream().findFirst().get().getName(), "Название фильма не совпадает");
    }

    @Test
    void shouldBeUpdatedFilmName() {
        Film film1 = createTestFilm1();
        filmController.create(film1);
        film1.setName("Updated movie name");
        filmController.update(film1);

        Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Количество фильмов не совпадает");
        assertSame("Updated movie name", films.stream().findFirst().get().getName(), "Название фильма не изменилось");
    }

    @Test
    void shouldNotBeCreatedFilmWithBlankName() {
        Film film = createTestFilm1();
        film.setName("");

        assertThrows(ValidationException.class, () -> filmController.create(film), "Валидация пустого названия фильма");
    }

    @Test
    void shouldNotBeCreatedFilmWithLongDescription() {
        Film film = createTestFilm1();
        film.setDescription("a".repeat(230));

        assertThrows(ValidationException.class, () -> filmController.create(film), "Валидация максимальной длины описания");
    }

    @Test
    void shouldNotBeCreatedFilmWithOldReleaseDate() {
        Film film = createTestFilm1();
        film.setReleaseDate(LocalDate.of(1775, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Дата релиза слишком старая");
    }

    @Test
    void shouldNotBeCreatedFilmWithNegativeDuration() {
        Film film = createTestFilm1();
        film.setDuration(-2);

        assertThrows(ValidationException.class, () -> filmController.create(film),
                "Продолжительность фильмадолжна быть положительной");
    }
}