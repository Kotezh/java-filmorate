package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper mapper;

    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genre_id";
    private static final String GET_BY_IDS_QUERY = "SELECT * FROM genres WHERE genre_id IN (:ids)";

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
}