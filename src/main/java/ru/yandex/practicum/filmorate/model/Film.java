package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Film {
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @StartDate(message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    private LinkedHashSet<Genre> genres;
    @NotNull
    private Mpa mpa;
    int likesCount;
}
