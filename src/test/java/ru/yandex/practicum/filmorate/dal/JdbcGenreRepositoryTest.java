package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
@Import({JdbcGenreRepository.class})
@DisplayName("JdbcGenreRepository")
class JdbcGenreRepositoryTest {
    public static final long TEST_GENRE_ID = 1L;
    private final JdbcGenreRepository jdbcGenreRepository;

    static Genre getTestGenre() {
        Genre genre = new Genre();
        genre.setId(TEST_GENRE_ID);
        genre.setName("Комедия");

        return genre;
    }

    private static List<Genre> getAllTestGenres() {
        ArrayList<Genre> genres = new ArrayList<>(6);

        genres.add(getTestGenre());

        Genre genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Драма");
        genres.add(genre2);

        Genre genre3 = new Genre();
        genre3.setId(3L);
        genre3.setName("Мультфильм");
        genres.add(genre3);

        Genre genre4 = new Genre();
        genre4.setId(4L);
        genre4.setName("Триллер");
        genres.add(genre4);

        Genre genre5 = new Genre();
        genre5.setId(5L);
        genre5.setName("Документальный");
        genres.add(genre5);

        Genre genre6 = new Genre();
        genre6.setId(6L);
        genre6.setName("Боевик");
        genres.add(genre6);

        return genres;
    }

    @Test
    @DisplayName("Должен найти информацию о жанре по заданному id")
    void shouldGetGenreById() {
        Optional<Genre> genreOptional = jdbcGenreRepository.getGenreById(TEST_GENRE_ID);

        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestGenre());
    }

    @Test
    @DisplayName("Должен найти все жанры")
    void shouldGetAllGenres() {
        List<Genre> genresFromDB = jdbcGenreRepository.getAllGenres();
        List<Genre> genresTest = getAllTestGenres();

        assertThat(genresFromDB).hasSize(genresTest.size());

        for (int i = 0; i < genresFromDB.size(); ++i) {
            assertThat(genresFromDB.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(genresTest.get(i));
        }
    }

    @Test
    @DisplayName("Должен найти информацию о нескольких жанрах по id")
    void shouldGetGenresByIds() {
        List<Genre> genresFromDB = jdbcGenreRepository.getGenresByIds(List.of(1L, 3L));
        List<Genre> genresTest = getAllTestGenres();

        assertThat(genresFromDB).hasSize(2);

        assertThat(genresFromDB.get(0))
                .usingRecursiveComparison()
                .isEqualTo(genresTest.get(0));

        assertThat(genresFromDB.get(1))
                .usingRecursiveComparison()
                .isEqualTo(genresTest.get(2));
    }
}