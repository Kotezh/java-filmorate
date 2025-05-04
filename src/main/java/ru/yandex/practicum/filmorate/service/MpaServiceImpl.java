package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final JdbcMpaRepository jdbcMpaRepository;

    @Override
    public Mpa getMpaById(long id) {
        return jdbcMpaRepository.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Некорректный id = " + id));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcMpaRepository.getAllMpa();
    }
}
