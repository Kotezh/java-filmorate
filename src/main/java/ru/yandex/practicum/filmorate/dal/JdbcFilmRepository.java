package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;
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
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id=:film_id";
    private static final String GET_BY_ID_QUERY = """
            SELECT f.*, r.mpa_name, COUNT(l.*) AS likes_count
            FROM films f JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            WHERE f.film_id = :film_id
            GROUP BY f.film_id, r.mpa_name
            """;
    private static final String GET_FILMS_QUERY = """
            SELECT f.*, r.mpa_name, COUNT(l.*) AS likes_count
            FROM films f
            JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            GROUP BY f.film_id, r.mpa_name
            """;
    private static final String SET_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES(:film_id, :genre_id);";
    private static final String GET_GENRE_QUERY = "SELECT g.genre_id, g.genre_name FROM film_genres fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = :film_id";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (user_id, film_id) VALUES(:user_id, :film_id)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id=:film_id AND user_id=:user_id;";

    private static final String SELECT_GENRES_BY_FILM_IDS_QUERY = """
            SELECT *
            FROM film_genres AS fg
            LEFT JOIN genres ON fg.genre_id = genres.genre_id
            WHERE fg.film_id IN (:film_ids)
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
    public void deleteFilm(long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        jdbc.update(DELETE_FILM_QUERY, params);
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

        connectGenres(films);
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

    private void connectGenres(Collection<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).toList();
        MapSqlParameterSource params = new MapSqlParameterSource("film_ids", filmIds);
        SqlRowSet rs = jdbc.queryForRowSet(SELECT_GENRES_BY_FILM_IDS_QUERY, params);

        Map<Long, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            filmsMap.get(filmId).getGenres().add(genre);
        }
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
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        List<Film> popularFilms;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);
        if (genreId != null) params.addValue("genreId", genreId);
        if (year != null) params.addValue("year", year);
        final String GET_POPULAR_FILMS_PREFIX_QUERY = """
                SELECT f.*, r.mpa_name, COUNT(l.user_id) AS likes_count
                FROM films f
                LEFT JOIN likes l ON l.film_id = f.film_id
                LEFT JOIN film_genres fg ON fg.film_id = f.film_id
                JOIN mpa r ON r.mpa_id = f.mpa_id
                WHERE f.film_id IS NOT NULL
                """;
        final String GET_POPULAR_FILMS_POSTFIX_QUERY = """
                GROUP BY f.film_id, r.mpa_name
                ORDER BY likes_count DESC
                LIMIT :count
                """;
        String GENRE_SQL = "AND fg.genre_id=:genreId";
        String YEAR_SQL = "AND EXTRACT(YEAR FROM f.release_date)=:year";
        String sql;

        if (genreId == null && year == null) {
            sql = GET_POPULAR_FILMS_PREFIX_QUERY + " " + GET_POPULAR_FILMS_POSTFIX_QUERY;
        } else if (genreId != null && year == null) {
            sql = GET_POPULAR_FILMS_PREFIX_QUERY + " " + GENRE_SQL + " " + GET_POPULAR_FILMS_POSTFIX_QUERY;
        } else if (genreId == null && year != null) {
            sql = GET_POPULAR_FILMS_PREFIX_QUERY + " " + YEAR_SQL + " " + GET_POPULAR_FILMS_POSTFIX_QUERY;
        } else {
            sql = GET_POPULAR_FILMS_PREFIX_QUERY + " " + GENRE_SQL + " " + YEAR_SQL + " " + GET_POPULAR_FILMS_POSTFIX_QUERY;
        }
        popularFilms = jdbc.query(sql, params, mapper);
        connectGenres(popularFilms);
        return popularFilms;
    }
}