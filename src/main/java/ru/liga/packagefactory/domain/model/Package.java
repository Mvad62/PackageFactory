package ru.liga.packagefactory.domain.model;

import java.util.List;
import java.util.Objects;

// Package.java - класс посылки
public class Package {
    private final String id;
    private final List<String> slices;
    private final int maxWidth;
    private final int height;
    private final int weight;
    private final double centerOfMassX;

    public Package(String id, List<String> slices) {
        this.id = Objects.requireNonNull(id);
        this.slices = validateSlices(slices);
        this.height = this.slices.size();
        this.maxWidth = calculateMaxWidth();
        this.weight = calculateWeight();
        this.centerOfMassX = calculateCenterOfMassX();
    }

    private List<String> validateSlices(List<String> slices) {
        if (slices == null || slices.isEmpty()) {
            throw new IllegalArgumentException("Package slices cannot be empty");
        }

        // Проверяем, что все символы в слоях одинаковые (кроме пробелов)
        char firstChar = findFirstNonSpaceChar(slices);

        for (String slice : slices) {
            for (int i = 0; i < slice.length(); i++) {
                char c = slice.charAt(i);
                if (c != ' ' && c != firstChar) {
                    throw new IllegalArgumentException("All non-space characters in package must be the same");
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
        throw new IllegalArgumentException("Package contains only spaces");
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

    private double calculateCenterOfMassX() {
        double totalMoment = 0;
        int totalWeight = 0;

        for (int y = 0; y < slices.size(); y++) {
            String slice = slices.get(y);
            for (int x = 0; x < slice.length(); x++) {
                if (slice.charAt(x) != ' ') {
                    totalMoment += x + 0.5; // Центр блока
                    totalWeight++;
                }
            }
        }

        return totalWeight == 0 ? 0 : totalMoment / totalWeight;
    }

    // Getters
    public String getId() { return id; }
    public List<String> getSlices() { return slices; }
    public int getMaxWidth() { return maxWidth; }
    public int getHeight() { return height; }
    public int getWeight() { return weight; }
    public double getCenterOfMassX() { return centerOfMassX; }
    public char getSymbol() { return findFirstNonSpaceChar(slices); }

    public String getSlice(int y) {
        return y < height ? slices.get(y) : "";
    }

    @Override
    public String toString() {
        return String.join("\n", slices);
    }
}