package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SlopeOne {
    private final Map<Long, Map<Long, Double>> diffMatrix = new HashMap<>();

    private final Map<Long, Map<Long, Integer>> freqMatrix = new HashMap<>();

    public void buildDifferences(Map<Long, Map<Long, Double>> data) {
        for (Map<Long, Double> userRatings : data.values()) {
            for (Map.Entry<Long, Double> e1 : userRatings.entrySet()) {
                long i = e1.getKey();
                double r1 = e1.getValue();

                diffMatrix.computeIfAbsent(i, k -> new HashMap<>());
                freqMatrix.computeIfAbsent(i, k -> new HashMap<>());

                for (Map.Entry<Long, Double> e2 : userRatings.entrySet()) {
                    long j = e2.getKey();
                    double r2 = e2.getValue();


                    diffMatrix.get(i).merge(j, r1 - r2, Double::sum);

                    freqMatrix.get(i).merge(j, 1, Integer::sum);
                }
            }
        }

        for (Long i : diffMatrix.keySet()) {
            for (Long j : diffMatrix.get(i).keySet()) {
                double totalDiff = diffMatrix.get(i).get(j);
                int count = freqMatrix.get(i).get(j);
                diffMatrix.get(i).put(j, totalDiff / count);
            }
        }
    }

    public Map<Long, Double> predictRatings(Map<Long, Double> userRatings) {
        Map<Long, Double> predictions = new HashMap<>();
        Map<Long, Integer> counts = new HashMap<>();

        for (Map.Entry<Long, Double> entry : userRatings.entrySet()) {
            long j = entry.getKey();
            double rj = entry.getValue();

            for (Map.Entry<Long, Map<Long, Double>> row : diffMatrix.entrySet()) {
                long i = row.getKey();
                Map<Long, Double> diffs = row.getValue();

                if (diffs.containsKey(j)) {
                    double diff = diffs.get(j);
                    int freq = freqMatrix.get(i).get(j);

                    predictions.merge(i, (diff + rj) * freq, Double::sum);
                    counts.merge(i, freq, Integer::sum);
                }
            }
        }

        for (Long i : predictions.keySet()) {
            predictions.put(i, predictions.get(i) / counts.get(i));
        }

        return predictions;
    }
}