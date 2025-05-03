package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcMpaRepository implements MpaRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final MpaRowMapper mapper;

    private static final String GET_ALL_MPA_QUERY = "SELECT * FROM mpa ORDER BY mpa_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = :mpa_id";

    @Override
    public Optional<Mpa> getMpaById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mpa_id", id);
        try (Stream<Mpa> stream = jdbc.queryForStream(GET_BY_ID_QUERY, params, mapper)) {
            return stream.findAny();
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbc.query(GET_ALL_MPA_QUERY, mapper);
    }
}