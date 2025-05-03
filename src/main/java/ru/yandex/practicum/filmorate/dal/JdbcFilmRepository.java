package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper mapper;
    private final GenreRowMapper genreRowMapper;

    private static final String CREATE_FILM_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES(:name,:description,:release_date,:duration,:mpa_id)";
    private static final String CLEAN_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id=:film_id";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name=:name, description=:description, release_date=:release_date, duration=:duration, mpa_id=:mpa_id WHERE film_id=:film_id";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, r.mpa_name, COUNT(l.*) AS likes_count, fg.genre_id AS genre_id, g.genre_name AS genre_name
            FROM films f
            JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            LEFT JOIN film_genres fg on f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            WHERE f.film_id = :film_id
            GROUP BY f.film_id, r.mpa_name
            """;
    private static final String GET_FILMS_QUERY = """
            SELECT f.*, r.mpa_name, fg.genre_id AS genre_id, g.genre_name AS genre_name, COUNT(l.film_id) AS likes_count
            FROM films f
            LEFT JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN film_genres fg on f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            GROUP BY f.film_id, fg.genre_id
            """;
    private static final String SET_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES(:film_id, :genre_id);";
    private static final String GET_GENRE_QUERY = "SELECT g.genre_id, g.genre_name FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = :film_id";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (user_id, film_id) VALUES(:user_id, :film_id)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id=:film_id AND user_id=:user_id;";
    private static final String GET_POPULAR_FILMS_QUERY = """
            SELECT f.*, r.mpa_name, fg.genre_id AS genre_id, g.genre_name AS genre_name, popular.likes_count
            FROM films f
            LEFT OUTER JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN film_genres fg on f.film_id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            JOIN (SELECT fl.film_id, COUNT(l.film_id) AS likes_count
                  FROM films fl
                  LEFT OUTER JOIN likes l ON l.film_id = fl.film_id
                  GROUP BY fl.film_id
                  ORDER BY COUNT(l.film_id) DESC
                  ) AS popular(film_id, likes_count) ON f.film_id = popular.film_id
            GROUP BY f.film_id, fg.genre_id
            ORDER BY popular.likes_count DESC
            LIMIT :limit;
            """;

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        jdbc.update(CREATE_FILM_QUERY, params, keyHolder);

        film.setId(keyHolder.getKeyAs(Long.class));
        if (film.getGenres() != null) {
            setFilmGenres(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("film_id", film.getId());

        jdbc.update(CLEAN_GENRES_QUERY, params, keyHolder);

        if (film.getGenres() != null) {
            setFilmGenres(film);
        }
        film.setGenres(new LinkedHashSet<>());
        jdbc.update(UPDATE_FILM_QUERY, params, keyHolder);

        return film;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", id);
        try (Stream<Film> stream = jdbc.queryForStream(GET_BY_ID_QUERY, params, mapper)) {
            Optional<Film> optionalFilm = stream.findAny();
            optionalFilm.ifPresent(film -> film.setGenres(getFilmGenres(film)));
            return optionalFilm;
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbc.query(GET_FILMS_QUERY, mapper);

        return films;
    }

    @Override
    public void setFilmGenres(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        jdbc.update(CLEAN_GENRES_QUERY, params);
        MapSqlParameterSource[] paramsList = film.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("genre_id", genre.getId())
                        .addValue("film_id", film.getId()))
                .toArray(MapSqlParameterSource[]::new);

        jdbc.batchUpdate(SET_GENRE_QUERY, paramsList, keyHolder);
    }

    @Override
    public LinkedHashSet<Genre> getFilmGenres(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        List<Genre> genres = jdbc.query(GET_GENRE_QUERY, params, genreRowMapper);
        LinkedHashSet<Genre> filmGenres = new LinkedHashSet<>(genres);
        film.setGenres(filmGenres);
        return filmGenres;
    }

    @Override
    public void addLike(long filmId, long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);
        jdbc.update(ADD_LIKE_QUERY, params, keyHolder);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);
        jdbc.update(DELETE_LIKE_QUERY, params, keyHolder);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", count);
        List<Film> popularFilms = jdbc.query(GET_POPULAR_FILMS_QUERY, params, mapper);
        return popularFilms;
    }
}