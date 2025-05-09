package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaRepository {
    Optional<Mpa> getMpaById(long id);

    List<Mpa> getAllMpa();
}
