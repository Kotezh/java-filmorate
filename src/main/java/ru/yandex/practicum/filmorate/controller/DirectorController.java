package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@Validated
public class DirectorController {

    private final DirectorServiceImpl directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Director create(@RequestBody Director director) {
        return directorService.create(director);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAll() {
        return directorService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getById(@PathVariable("id") long id) {
        return directorService.getById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDirector(@PathVariable("id") long id) {
        directorService.delete(id);
    }
}
