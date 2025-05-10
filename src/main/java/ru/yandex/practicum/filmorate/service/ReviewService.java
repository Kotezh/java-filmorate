package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    public Optional<Review> getReviewById(long reviewId);

    public List<Review> getAllReviewsByFilmId(long filmId, long count);

    public Review create(Review review);

    public Review update(Review review);

    public void deleteReview(long reviewId);

    public void addLike(long reviewId, long userId);

    public void addDislike(long reviewId, long userId);

    public void deleteLike(long reviewId, long userId);

    public void deleteDislike(long reviewId, long userId);
}
