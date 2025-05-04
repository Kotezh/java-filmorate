package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Validated
public class GenreController {
    private final GenreService genreService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public Genre getGenreById(@PathVariable("id") long id) {
        return genreService.getGenreById(id);
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getAllGenres();
    }
}
