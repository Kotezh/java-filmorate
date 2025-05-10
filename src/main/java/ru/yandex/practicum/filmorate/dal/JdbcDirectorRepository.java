package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final DirectorRowMapper mapper;

    private static final String CREATE_DIRECTOR_QUERY = "INSERT INTO directors (director_name) VALUES(:director_name)";
    private static final String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM directors ORDER BY director_id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = :director_id";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET director_name=:director_name WHERE director_id=:director_id";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE director_id = :director_id";

    @Override
    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("director_name", director.getName());
        jdbc.update(CREATE_DIRECTOR_QUERY, params, keyHolder);
        director.setId(keyHolder.getKeyAs(Long.class));
        return director;
    }

    @Override
    public List<Director> getAll() {
        return jdbc.query(GET_ALL_DIRECTORS_QUERY, mapper);
    }

    @Override
    public Optional<Director> getById(Long directorId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", directorId);
        try (Stream<Director> stream = jdbc.queryForStream(GET_BY_ID_QUERY, params, mapper)) {
            return stream.findAny();
        }

    }

    @Override
    public Director update(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_name", director.getName());
        params.addValue("director_id", director.getId());
        jdbc.update(UPDATE_DIRECTOR_QUERY, params);
        return director;
    }

    @Override
    public void delete(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", id);
        jdbc.update(DELETE_DIRECTOR_QUERY, params);

    }
}
