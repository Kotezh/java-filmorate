package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final ReviewRowMapper reviewMapper;

    private static final String GET_BY_ID_REVIEW = """
            SELECT
                r.reviewId,
                r.content,
                r.isPositive,
                r.userId,
                r.filmId,
                COALESCE(SUM(rl.reaction_type), 0) AS useful
            FROM reviews r
            LEFT JOIN reviews_likes rl ON r.reviewId = rl.reviewId
            WHERE r.reviewId = :reviewId
            GROUP BY r.reviewId, r.content, r.isPositive, r.userId, r.filmId;
            """;

    private static final String GET_ALL_REVIEW = """
            SELECT
                r.reviewId,
                r.content,
                r.isPositive,
                r.userId,
                r.filmId,
                COALESCE(SUM(rl.reaction_type), 0) AS useful
            FROM reviews r
            LEFT JOIN reviews_likes rl ON r.reviewId = rl.reviewId
            GROUP BY r.reviewId, r.content, r.isPositive, r.userId, r.filmId;
            """;

    private static final String CREATE_REVIEW = """
            INSERT INTO reviews (content, isPositive, userId, filmId, useful)
            VALUES(:content,:isPositive,:userId,:filmId,:useful)
            """;
    private static final String UPDATE_REVIEW = """
            UPDATE reviews
            SET content=:content, isPositive=:isPositive, userId=:userId, filmId=:filmId, useful=:useful
            WHERE reviewId=:reviewId
            """;
    private static final String DELETE_REVIEW = """
            DELETE FROM reviews
            WHERE reviewId=:reviewId
            """;
    private static final String ADD_LIKE_REVIEW = """
            INSERT INTO reviews_likes (reviewId, userId, reaction_type)
            VALUES(:reviewId, :userId, 1)
            """;
    private static final String ADD_DISLIKE_REVIEW = """
            INSERT INTO reviews_likes (reviewId, userId, reaction_type)
            VALUES(:reviewId, :userId, -1)
            """;
    private static final String DELETE_LIKE_REVIEW = """
            DELETE FROM reviews_likes
            WHERE reviewId=:reviewId AND userId=:userId
            """;


    @Override
    public Optional<Review> getReviewById(long reviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", reviewId);
        try (Stream<Review> stream = jdbc.queryForStream(GET_BY_ID_REVIEW, params, reviewMapper)) {
            return stream.findAny();
        }
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId, long count) {
        return jdbc.query(GET_ALL_REVIEW, reviewMapper).reversed();
    }

    @Override
    public Review create(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("content", review.getContent());
        params.addValue("isPositive", review.getIsPositive());
        params.addValue("userId", review.getUserId());
        params.addValue("filmId", review.getFilmId());
        params.addValue("useful", review.getUseful());

        jdbc.update(CREATE_REVIEW, params, keyHolder);
        review.setReviewId(keyHolder.getKeyAs(Long.class));

        return review;
    }

    @Override
    public Review update(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("content", review.getContent());
        params.addValue("isPositive", review.getIsPositive());
        params.addValue("userId", review.getUserId());
        params.addValue("filmId", review.getFilmId());
        params.addValue("useful", review.getUseful());
        params.addValue("reviewId", review.getReviewId());

        jdbc.update(UPDATE_REVIEW, params, keyHolder);

        return review;
    }

    @Override
    public void deleteReview(long reviewId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", reviewId);
        jdbc.update(DELETE_REVIEW, params);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        deleteReaction(reviewId, userId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", reviewId);
        params.addValue("userId", userId);
        jdbc.update(ADD_LIKE_REVIEW, params);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        deleteReaction(reviewId, userId);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", reviewId);
        params.addValue("userId", userId);
        jdbc.update(ADD_DISLIKE_REVIEW, params);
    }

    @Override
    public void deleteReaction(long reviewId, long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reviewId", reviewId);
        params.addValue("userId", userId);
        jdbc.update(DELETE_LIKE_REVIEW, params);
    }

}
