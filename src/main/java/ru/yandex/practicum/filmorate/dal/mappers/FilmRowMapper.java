package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException, DataAccessException {
        Film film = null;
        while (resultSet.next()) {
            if (film == null) {
                Mpa mpa = new Mpa();
                mpa.setId(resultSet.getLong("mpa_id"));
                mpa.setName(resultSet.getString("mpa_name"));
                film = new Film();
                film.setId(resultSet.getLong("film_id"));
                film.setName(resultSet.getString("name"));
                film.setDescription(resultSet.getString("description"));
                film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
                film.setDuration(resultSet.getInt("duration"));
                film.setMpa(mpa);
                film.setGenres(new LinkedHashSet<>());
                film.setLikesCount(resultSet.getInt("likes_count"));
            }
            Genre genre = new Genre();
            genre.setId(resultSet.getLong("genre_id"));
            genre.setName(resultSet.getString("genre_name"));
            if (genre.getId() == 0) {
                return film;
            }
            film.getGenres().add(genre);
        }
        return film;
    }
}