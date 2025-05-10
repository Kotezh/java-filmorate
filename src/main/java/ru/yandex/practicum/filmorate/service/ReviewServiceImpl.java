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
public class ReviewServiceImpl implements ReviewService {
    private final JdbcReviewRepository jdbcReviewRepository;
    private final JdbcFilmRepository jdbcFilmRepository;
    private final JdbcUserRepository jdbcUserRepository;

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        checkReviewId(reviewId);
        return jdbcReviewRepository.getReviewById(reviewId);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, long count) {
        checkFilmId(filmId);
        return jdbcReviewRepository.getAllReviewsByFilmId(filmId, count);
    }

    @Override
    public Review create(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        return jdbcReviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        return jdbcReviewRepository.update(review);
    }

    @Override
    public void deleteReview(long reviewId) {
        checkReviewId(reviewId);
        jdbcReviewRepository.deleteReview(reviewId);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        jdbcReviewRepository.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        jdbcReviewRepository.addDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        jdbcReviewRepository.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        checkReviewId(reviewId);
        checkUserId(userId);
        jdbcReviewRepository.deleteDislike(reviewId, userId);
    }

    private void checkUserId(long id) {
        jdbcUserRepository.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private void checkFilmId(long id) {
        jdbcFilmRepository.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    private void checkReviewId(long id) {
        jdbcReviewRepository.getReviewById(id).orElseThrow(() -> new NotFoundException("Отзыв не найден"));
    }

}
