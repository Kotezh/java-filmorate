package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    Director create(Director director);

    List<Director> getAll();

    Director getById(Long directorId);

    Director update(Director director);

    void delete(Long id);
}
