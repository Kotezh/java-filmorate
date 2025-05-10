package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorRepository {
    Director create(Director director);

    List<Director> getAll();

    Optional<Director> getById(Long directorId);

    Director update(Director director);

    void delete(Long id);
}
