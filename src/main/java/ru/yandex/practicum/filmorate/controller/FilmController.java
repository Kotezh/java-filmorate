package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus (HttpStatus.CREATED)
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable("filmId") long filmId, @PathVariable("userId") long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") final Integer count) {
        return filmService.findPopularFilms(count);
    }
}
