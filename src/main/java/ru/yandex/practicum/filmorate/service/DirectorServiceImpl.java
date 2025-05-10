package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.JdbcDirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements  DirectorService{

    private final JdbcDirectorRepository jdbcDirectorRepository;

    @Override
    public Director create(Director director){
        if (director.getName() == null || director.getName().isBlank()){
            throw new ValidationException("Некоректное имя режисёра");
        }
        log.info("Создан новый режисёр");
        return jdbcDirectorRepository.create(director);
    }
    @Override
    public List<Director> getAll(){
        return jdbcDirectorRepository.getAll();
    }
    @Override
    public Director getById(Long directorId){
        return jdbcDirectorRepository.getById(directorId).
                orElseThrow(() -> new NotFoundException("Некорректный id = " + directorId));
    }
    @Override
    public Director update(Director director){
        if (director.getId() == -1) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (jdbcDirectorRepository.getAll().stream().anyMatch((oldDirector) -> oldDirector.getId() == director.getId())) {
            log.info("Пользователь обновлён");
            return jdbcDirectorRepository.update(director);
        }
        throw new NotFoundException("Режиссёр с id = " + director.getId() + " не найден");
    }
    @Override
    public void delete(Long id){
        getById(id);
        jdbcDirectorRepository.delete(id);
        log.info("Режиссёр удалён");
    }
    }

