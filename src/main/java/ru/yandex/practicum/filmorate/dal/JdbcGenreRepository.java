package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper mapper;

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genre_id";
    private static final String GET_BY_IDS_QUERY = "SELECT * FROM genres WHERE genre_id IN (:ids)";
    private static final String GET_FILM_GENRES_QUERY = "SELECT g.genre_id, g.genre_name, fg.film_id FROM genres AS g JOIN film_genres AS fg ON g.genre_id = fg.genre_id";

    @Override
    public Optional<Genre> getGenreById(long id) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genre_id", id);
        try (Stream<Genre> stream = jdbc.queryForStream(GET_BY_ID_QUERY, params, mapper)) {
            return stream.findAny();
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbc.query(GET_ALL_GENRES_QUERY, mapper);
    }

    @Override
    public List<Genre> getGenresByIds(List<Long> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        try (Stream<Genre> stream = jdbc.queryForStream(GET_BY_IDS_QUERY, params, mapper)) {
            return stream.toList();
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(
                resultSet.getLong("genre_id"),
                resultSet.getString("genre_name")
        );
    }

    @Override
    public List<Film> load(List<Film> films) {
        if (films.isEmpty()) {
            return films;
        }
        Map<Long, LinkedHashSet<Genre>> genresByFilmId = new HashMap<>();

        jdbc.query(GET_FILM_GENRES_QUERY, (rs) -> {
            long filmId = rs.getLong("film_id");
            Genre genre = makeGenre(rs, 0);
            genresByFilmId.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        });
        for (Film film : films) {
            LinkedHashSet<Genre> genres = genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>());
            film.setGenres(genres);
        }
        return films;
    }
}