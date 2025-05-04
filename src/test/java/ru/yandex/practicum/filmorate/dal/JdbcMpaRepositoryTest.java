package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate.dal.mappers")
@Import({JdbcMpaRepository.class})
@DisplayName("JdbcMpaRepository")
class JdbcMpaRepositoryTest {
    public static final long TEST_RATING_ID = 1L;
    private final JdbcMpaRepository jdbcMpaRepository;

    static Mpa getTestRating() {
        Mpa mpa = new Mpa();
        mpa.setId(TEST_RATING_ID);
        mpa.setName("G");

        return mpa;
    }

    private static List<Mpa> getTestAllRatings() {
        ArrayList<Mpa> allRatings = new ArrayList<>(3);

        allRatings.add(getTestRating());

        Mpa rating2 = new Mpa();
        rating2.setId(2L);
        rating2.setName("PG");
        allRatings.add(rating2);

        Mpa rating3 = new Mpa();
        rating3.setId(3L);
        rating3.setName("PG-13");
        allRatings.add(rating3);

        Mpa rating4 = new Mpa();
        rating4.setId(4L);
        rating4.setName("R");
        allRatings.add(rating4);

        Mpa rating5 = new Mpa();
        rating5.setId(5L);
        rating5.setName("NC-17");
        allRatings.add(rating5);

        return allRatings;
    }

    @Test
    @DisplayName("Должен найти информацию о рейтинге по id")
    void shouldGetMpaRatingById() {
        Optional<Mpa> mpaOptional = jdbcMpaRepository.getMpaById(TEST_RATING_ID);

        assertThat(mpaOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(getTestRating());
    }

    @Test
    @DisplayName("Должен найти информацию обо всех рейтингах")
    void shouldGetAllMpaRatings() {
        List<Mpa> mpaList = jdbcMpaRepository.getAllMpa();
        List<Mpa> testMpaList = getTestAllRatings();

        assertThat(mpaList).hasSize(testMpaList.size());

        for (int i = 0; i < mpaList.size(); ++i) {
            assertThat(mpaList.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(testMpaList.get(i));
        }
    }
}