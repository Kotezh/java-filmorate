package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Validated
public class ReviewController {
    private final ReviewServiceImpl reviewService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Review> getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getAllReviewsByFilmId(@RequestParam(value = "filmId") long filmId,
                                              @RequestParam(value = "count", defaultValue = "10") long count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDislike(@PathVariable long reviewId, @PathVariable long userId) {
        reviewService.deleteDislike(reviewId, userId);
    }

}
