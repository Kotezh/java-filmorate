package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper mapper;
    private final GenreRowMapper genreRowMapper;
    private final DirectorRowMapper directorRowMapper;

    private static final String CREATE_FILM_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES(:name,:description,:release_date,:duration,:mpa_id)";
    private static final String SET_DIRECTORS_QUERY = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES(:film_id, :director_id)";
    private static final String GET_DIRECTORS_QUERY = "SELECT d.DIRECTOR_ID, d.DIRECTOR_NAME  FROM FILM_DIRECTORS fd  JOIN DIRECTORS d  ON fd.DIRECTOR_ID  = d.DIRECTOR_ID  WHERE fd.film_id = :film_id";
    private static final String CLEAN_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id=:film_id";
    private static final String CLEAN_DIRECTORS_QUERY = "DELETE FROM film_directors WHERE film_id=:film_id";
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
    private static final String SELECT_DIRECTORS_BY_FILM_IDS_QUERY = """
            SELECT *
            FROM FILM_DIRECTORS fd
            LEFT JOIN DIRECTORS d  ON fd.DIRECTOR_ID  = d.DIRECTOR_ID
            WHERE fd.film_id IN (:film_ids)
            """;
    private static final String GET_DIRECTOR_FILMS_BY_YEAR = """
            SELECT f.*,r.MPA_NAME,COUNT(l.user_id) AS likes_count
            FROM FILMS f
            JOIN FILM_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID
            JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            WHERE fd.DIRECTOR_ID = :director_id
            GROUP BY f.FILM_ID
            ORDER BY f.RELEASE_DATE
            """;
    private static final String GET_DIRECTOR_FILMS_BY_LIKES = """
            SELECT f.*,r.MPA_NAME,COUNT(l.user_id) AS likes_count
            FROM FILMS f
            JOIN FILM_DIRECTORS fd ON fd.FILM_ID = f.FILM_ID
            JOIN mpa r ON f.mpa_id = r.mpa_id
            LEFT JOIN likes l ON l.film_id = f.film_id
            WHERE fd.DIRECTOR_ID = :director_id
            GROUP BY f.FILM_ID
            ORDER BY likes_count DESC
            """;

    private static final String GET_COMMON_FILMS_QUERY = """
            SELECT f.*, r.mpa_name, COUNT(l1.user_id) AS likes_count
            FROM films f
            JOIN mpa r ON r.mpa_id = f.mpa_id
            JOIN likes l1 ON l1.film_id = f.film_id AND l1.user_id = :userId
            JOIN likes l2 ON l2.film_id = f.film_id AND l2.user_id = :friendId
            GROUP BY f.film_id, r.mpa_name
            ORDER BY f.name;
            """;

    private static final String ACTIVITY_GENERAL =
            "INSERT INTO activity (userId, entityId, eventType, operation, timestamp)";

    private static final String ACTIVITY_FILM_LIKE = ACTIVITY_GENERAL +
            """
                    VALUES(:userId, :entityId, 'LIKE', 'ADD',
                    """ + instantOfSecond() + ")";

    private static final String ACTIVITY_FILM_LIKE_DELETE = ACTIVITY_GENERAL +
            """
                    VALUES(:userId, :entityId, 'LIKE', 'REMOVE',
                    """ + instantOfSecond() + ")";

    private static long instantOfSecond() {
        return Instant.now().toEpochMilli();
    }

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
        if (film.getDirectors() != null) {
            setFilmDirectors(film);
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
        jdbc.update(CLEAN_DIRECTORS_QUERY, params, keyHolder);

        if (film.getGenres() != null) {
            setFilmGenres(film);
        }
        if (film.getDirectors() != null) {
            setFilmDirectors(film);
        }
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
            optionalFilm.ifPresent(film -> film.setDirectors(getFilmDirectors(film)));
            return optionalFilm;
        }
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = jdbc.query(GET_FILMS_QUERY, mapper);
        connectDirectors(films);
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
    public void setFilmDirectors(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        jdbc.update(CLEAN_DIRECTORS_QUERY, params);
        MapSqlParameterSource[] paramsList = film.getDirectors().stream()
                .map(director -> new MapSqlParameterSource()
                        .addValue("director_id", director.getId())
                        .addValue("film_id", film.getId()))
                .toArray(MapSqlParameterSource[]::new);

        jdbc.batchUpdate(SET_DIRECTORS_QUERY, paramsList, keyHolder);
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
    public LinkedHashSet<Director> getFilmDirectors(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        List<Director> directors = jdbc.query(GET_DIRECTORS_QUERY, params, directorRowMapper);
        LinkedHashSet<Director> filmDirectors = new LinkedHashSet<>(directors);
        film.setDirectors(filmDirectors);
        return filmDirectors;
    }

    @Override
    public void connectGenres(Collection<Film> films) {
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
    public void connectDirectors(Collection<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).toList();
        MapSqlParameterSource params = new MapSqlParameterSource("film_ids", filmIds);
        SqlRowSet rs = jdbc.queryForRowSet(SELECT_DIRECTORS_BY_FILM_IDS_QUERY, params);
        Map<Long, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            Director director = new Director(rs.getLong("director_id"), rs.getString("director_name"));
            filmsMap.get(filmId).getDirectors().add(director);
        }
    }

    @Override
    public void addLike(long filmId, long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);
        jdbc.update(ADD_LIKE_QUERY, params, keyHolder);

        MapSqlParameterSource params2 = new MapSqlParameterSource();
        params2.addValue("userId", userId);
        params2.addValue("entityId", filmId);

        jdbc.update(ACTIVITY_FILM_LIKE, params2);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("film_id", filmId);
        jdbc.update(DELETE_LIKE_QUERY, params, keyHolder);

        MapSqlParameterSource params2 = new MapSqlParameterSource();
        params2.addValue("userId", userId);
        params2.addValue("entityId", filmId);

        jdbc.update(ACTIVITY_FILM_LIKE_DELETE, params2);
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
        final String GENRE_SQL = "AND fg.genre_id=:genreId";
        final String YEAR_SQL = "AND EXTRACT(YEAR FROM f.release_date)=:year";
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
        connectDirectors(popularFilms);
        return popularFilms;
    }

    @Override
    public List<Film> getDirectorFilmsByYear(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", id);
        List<Film> directorFilmsByYear = jdbc.query(GET_DIRECTOR_FILMS_BY_YEAR, params, mapper);
        connectGenres(directorFilmsByYear);
        connectDirectors(directorFilmsByYear);
        return directorFilmsByYear;
    }

    @Override
    public List<Film> getDirectorFilmsByLikes(long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", id);
        List<Film> directorFilmsByYear = jdbc.query(GET_DIRECTOR_FILMS_BY_LIKES, params, mapper);
        connectGenres(directorFilmsByYear);
        connectDirectors(directorFilmsByYear);
        return directorFilmsByYear;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);
        List<Film> commonFilms = jdbc.query(GET_COMMON_FILMS_QUERY, params, mapper);

        connectGenres(commonFilms);
        connectDirectors(commonFilms);
        return commonFilms;
    }
}