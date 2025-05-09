package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Optional<Review> getReviewById(long reviewId);

    List<Review> getAllReviewsByFilmId(long filmId, long count);

    Review create(Review review);

    Review update(Review review);

    void deleteReview(long reviewId);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
