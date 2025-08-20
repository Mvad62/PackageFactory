package ru.liga.packagefactory.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Package.java - класс посылки
public class Package {
    private final String id;
    private final List<String> slices;
    private final int height;
    private final int weight;

    @JsonCreator
    public Package(@JsonProperty("id") String id,
                   @JsonProperty("slices") List<String> slices) {
        this.id = Objects.requireNonNull(id);
        this.slices = validateSlices(slices);
        this.height = this.slices.size();
        this.weight = calculateWeight();
    }

    private List<String> validateSlices(List<String> slices) {
        if (slices == null || slices.isEmpty()) {
            throw new IllegalArgumentException("Слой посылки не может быть пустым");
        }

        // Проверяем, что все символы в слоях одинаковые (кроме пробелов)
        char firstChar = findFirstNonSpaceChar(slices);

        for (String slice : slices) {
            for (int i = 0; i < slice.length(); i++) {
                char c = slice.charAt(i);
                if (c != ' ' && c != firstChar) {
                    throw new IllegalArgumentException("Все символы посылки должны быть одинаковы");
                }
            }
        }

        return slices;
    }

    private char findFirstNonSpaceChar(List<String> slices) {
        for (String slice : slices) {
            for (int i = 0; i < slice.length(); i++) {
                if (slice.charAt(i) != ' ') {
                    return slice.charAt(i);
                }
            }
        }
        throw new IllegalArgumentException("Посылка не содержит символов");
    }

    public int calculateMaxWidth() {
        int maxRight = 0;
        int minLeft = Integer.MAX_VALUE;

        for (String slice : slices) {
            int firstNonSpace = slice.indexOf(getSymbol());
            int lastNonSpace = slice.lastIndexOf(getSymbol());

            if (firstNonSpace != -1) {
                minLeft = Math.min(minLeft, firstNonSpace);
                maxRight = Math.max(maxRight, lastNonSpace);
            }
        }

        return minLeft == Integer.MAX_VALUE ? 0 : maxRight - minLeft + 1;
    }

    private int calculateWeight() {
        return slices.stream()
                .mapToInt(s -> (int) s.chars().filter(c -> c != ' ').count())
                .sum();
    }

    // Getters
    public String getId() {
        return id;
    }
    public List<String> getSlices() {
        return Collections.unmodifiableList(slices);
    }
    public int getMaxWidth() {
        return calculateMaxWidth();
    }
    public int getHeight() {
        return height;
    }
    public int getWeight() {
        return weight;
    }
    public char getSymbol() {
        return findFirstNonSpaceChar(slices);
    }

    public String getSlice(int y) {
        return y < height ? slices.get(y) : "";
    }

    @Override
    public String toString() {
        return String.join("\n", slices);
    }
}