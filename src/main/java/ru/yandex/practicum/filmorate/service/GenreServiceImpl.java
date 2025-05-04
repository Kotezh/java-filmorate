package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final JdbcGenreRepository jdbcGenreRepository;

    @Override
    public Genre getGenreById(long id) {
        return jdbcGenreRepository.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Некорректный id = " + id));
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcGenreRepository.getAllGenres();
    }
}