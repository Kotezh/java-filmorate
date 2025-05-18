package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Optional<Review> getReviewById(long reviewId);

    List<Review> getAllReviewsByFilmId(long filmId, long count);

    Review create(Review review);

    Review update(Review review);

    void deleteReview(long reviewId);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteReaction(long reviewId, long userId);

}
