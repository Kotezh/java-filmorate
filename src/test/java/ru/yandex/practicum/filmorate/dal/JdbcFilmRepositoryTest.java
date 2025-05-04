package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
@Import({JdbcFilmRepository.class})
@DisplayName("JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    public static final long TEST_FILM_ID = 1L;
    private final JdbcFilmRepository jdbcFilmRepository;

    static Film getTestFilm() {
        Film film = new Film();

        film.setId(TEST_FILM_ID);
        film.setName("Фильм 1");
        film.setDescription("Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1 Описание фильма 1");
        film.setReleaseDate(LocalDate.of(2000, 5, 5));
        film.setDuration(127);

        Mpa mpa = new Mpa();
        mpa.setId(3L);
        mpa.setName("PG-13");
        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>());
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        film.getGenres().add(genre);
        film.setLikesCount(1);

        return film;
    }

    private static Film getTestFilmToCreateOrUpdate() {
        Film film = new Film();

        film.setId(TEST_FILM_ID);
        film.setName("Фильм 2");
        film.setDescription("Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2");
        film.setReleaseDate(LocalDate.of(1950, 12, 12));
        film.setDuration(138);

        Mpa mpa = new Mpa();
        mpa.setId(3L);
        mpa.setName("PG-13");
        film.setMpa(mpa);

        film.setGenres(new LinkedHashSet<>());
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("Комедия");
        film.getGenres().add(genre);

        film.setLikesCount(1);

        return film;
    }

    private static List<Film> getAllTestFilms() {
        ArrayList<Film> films = new ArrayList<>();

        films.add(getTestFilm());

        Genre genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Комедия");

        Genre genre3 = new Genre();
        genre3.setId(3L);
        genre3.setName("Мультфильм");

        Film film1 = new Film();
        film1.setId(2);
        film1.setName("Фильм 2");
        film1.setDescription("Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2 Описание фильма 2");
        film1.setReleaseDate(LocalDate.of(1950, 12, 12));
        film1.setDuration(138);
        Mpa mpa2 = new Mpa();
        mpa2.setId(2L);
        mpa2.setName("PG");
        film1.setMpa(mpa2);
        film1.setGenres(new LinkedHashSet<>());
        film1.getGenres().add(genre1);
        film1.setLikesCount(3);
        films.add(film1);

        Film film2 = new Film();
        film2.setId(3);
        film2.setName("Фильм 3");
        film2.setDescription("Описание фильма 3 Описание фильма 3 Описание фильма 3");
        film2.setReleaseDate(LocalDate.of(2023, 11, 11));
        film2.setDuration(220);
        Mpa mpa3 = new Mpa();
        mpa3.setId(2L);
        mpa3.setName("PG");
        film2.setMpa(mpa3);

        film2.setGenres(new LinkedHashSet<>());
        film2.getGenres().add(genre1);
        film2.getGenres().add(genre3);
        film2.setLikesCount(2);
        films.add(film2);

        return films;
    }


    @Test
    @DisplayName("Должен найти информацию о фильме по заданному id")
    void shouldGetFilmById() {
        Optional<Film> optionalFilm = jdbcFilmRepository.getFilmById(TEST_FILM_ID);

        assertThat(optionalFilm)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());
    }

    @Test
    @DisplayName("Должен найти информацию обо всех фильмах")
    void shouldGetAllFilms() {
        List<Film> filmsFromDB = jdbcFilmRepository.getAllFilms();
        List<Film> filmsTest = getAllTestFilms();

        assertThat(filmsFromDB).hasSize(filmsTest.size());

        for (int i = 0; i < filmsFromDB.size(); ++i) {
            assertThat(filmsFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(filmsTest.get(i));
        }
    }

    @Test
    @DisplayName("Должен создавать фильм")
    void shouldCreateFilm() {
        Film testFilm = getTestFilmToCreateOrUpdate();
        Film createdFilm = jdbcFilmRepository.create(testFilm);
        Optional<Film> filmFromDB = jdbcFilmRepository.getFilmById(createdFilm.getId());

        assertThat(filmFromDB)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);

        testFilm.setLikesCount(1);
        assertThat(createdFilm)
                .usingRecursiveComparison()
                .ignoringFields("user_id")
                .isEqualTo(testFilm);

    }

    @Test
    @DisplayName("Должен обновить фильм")
    void shouldUpdateFilm() {
        Film filmBeforeUpdate = jdbcFilmRepository.getFilmById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id = " + TEST_FILM_ID));

        assertThat(filmBeforeUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestFilm());

        jdbcFilmRepository.update(getTestFilmToCreateOrUpdate());

        Film filmAfterUpdate = jdbcFilmRepository.getFilmById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id = " + TEST_FILM_ID));

        assertThat(filmAfterUpdate)
                .usingRecursiveComparison()
                .isEqualTo(getTestFilmToCreateOrUpdate());
    }

    @Test
    @DisplayName("Должен добавлять записи о лайках")
    void shouldAddLike() {
        Film film = jdbcFilmRepository.getFilmById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(1);

        jdbcFilmRepository.addLike(TEST_FILM_ID, 2);

        film = jdbcFilmRepository.getFilmById(TEST_FILM_ID)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id == " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен удалять лайки")
    void shouldDeleteLikeRecord() {
        Film film = jdbcFilmRepository.getFilmById(2)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(3);

        jdbcFilmRepository.deleteLike(2, 1);

        film = jdbcFilmRepository.getFilmById(2)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: id = " + TEST_FILM_ID));

        assertThat(film.getLikesCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен возвращать сортированный список популярных фильмов")
    void shouldGetPopularFilms() {
        List<Film> popularFilms = jdbcFilmRepository.getPopularFilms(2);
        List<Film> filmsTest = getAllTestFilms();

        assertThat(popularFilms).hasSize(2);

        assertThat(popularFilms.get(0))
                .usingRecursiveComparison()
                .isEqualTo(filmsTest.get(1));
        assertThat(popularFilms.get(1))
                .usingRecursiveComparison()
                .isEqualTo(filmsTest.get(2));
    }
}