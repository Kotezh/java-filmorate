package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.dal.JdbcReviewRepository;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ReviewServiceImpl {
    private final JdbcReviewRepository jdbcReviewRepository;
    private final JdbcFilmRepository jdbcFilmRepository;
    private final JdbcUserRepository jdbcUserRepository;

    public Optional<Review> getReviewById(long reviewId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        return jdbcReviewRepository.getReviewById(reviewId);
    }

    public List<Review> getAllReviewsByFilmId(long filmId, long count) {
        if (jdbcFilmRepository.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return jdbcReviewRepository.getAllReviewsByFilmId(filmId, count);
    }

    public Review create(Review review) {
        if (jdbcUserRepository.getUserById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (jdbcFilmRepository.getFilmById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return jdbcReviewRepository.create(review);
    }

    public Review update(Review review) {
        if (jdbcUserRepository.getUserById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (jdbcFilmRepository.getFilmById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return jdbcReviewRepository.update(review);
    }

    public void deleteReview(long reviewId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        jdbcReviewRepository.deleteReview(reviewId);
    }

    public void addLike(long reviewId, long userId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        if (jdbcUserRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + reviewId + " не найден");
        }
        jdbcReviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(long reviewId, long userId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        if (jdbcUserRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + reviewId + " не найден");
        }
        jdbcReviewRepository.addDislike(reviewId, userId);
    }

    public void deleteLike(long reviewId, long userId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        if (jdbcUserRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + reviewId + " не найден");
        }
        jdbcReviewRepository.deleteLike(reviewId, userId);
    }

    public void deleteDislike(long reviewId, long userId) {
        if (jdbcReviewRepository.getReviewById(reviewId).isEmpty()) {
            throw new NotFoundException("Отзыв с id = " + reviewId + " не найден");
        }
        if (jdbcUserRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + reviewId + " не найден");
        }
        jdbcReviewRepository.deleteDislike(reviewId, userId);
    }
}
