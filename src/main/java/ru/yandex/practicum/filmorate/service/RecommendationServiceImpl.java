package ru.yandex.practicum.filmorate.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.JdbcUserRepository;
import ru.yandex.practicum.filmorate.dal.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final JdbcUserRepository userRepository;
    private final JdbcFilmRepository filmRepository;
    private final SlopeOne slopeOne;

    @PostConstruct
    public void init() {
        slopeOne.buildDifferences(Collections.emptyMap());
    }

    private void rebuildModel() {
        var raw = userRepository.getAllUserLikes();
        var data = raw.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().collect(Collectors.toMap(f -> f, f -> 1.0))
                ));
        slopeOne.buildDifferences(data);
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        var myLikes = userRepository.getAllUserLikes()
                .getOrDefault(userId, Collections.emptySet());
        if (myLikes.isEmpty()) {
            return Collections.emptyList();
        }

        rebuildModel();

        var predictions = slopeOne.predictRatings(
                myLikes.stream().collect(Collectors.toMap(f -> f, f -> 1.0))
        );
        predictions.keySet().removeAll(myLikes);

        if (predictions.isEmpty()) {
            return Collections.emptyList();
        }

        var recommendations = predictions.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(e -> filmRepository.getFilmById(e.getKey()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        filmRepository.connectGenres(recommendations);
        filmRepository.connectDirectors(recommendations);

        return recommendations;
    }
}