package ru.liga.packagefactory.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

// Truck.java - класс кузова машины
public class Truck {
    private final String id;
    private final int width;
    private final int height;
    private final char[][] grid;
    private final List<Package> packages;
    private int usedSpace = 0;
    private int currentWeight = 0;
    private final Map<String, TruckExportData.Position> packagePositions = new HashMap<>();

    public static final int DEFAULT_WIDTH = 6;
    public static final int DEFAULT_HEIGHT = 6;
    public static final int MAX_CAPACITY = DEFAULT_WIDTH * DEFAULT_HEIGHT;

    public Truck (String id){
        super();
        this.id = id;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.grid = new char[height][width];
        this.packages = new ArrayList<>();
        initializeGrid();
    }

    @JsonCreator
    public Truck(@JsonProperty("id") String id,
                 @JsonProperty("width") int width,
                 @JsonProperty("height") int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.grid = new char[height][width];
        this.packages = new ArrayList<>();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = ' ';
            }
        }
    }

    public boolean placePackage(Package pkg, int x, int y) {
        // Проверяем выход за границы грузовика
        if (x < 0 || y < 0 ||
                x + pkg.getMaxWidth() > width ||
                y + pkg.getHeight() > height) {
            return false;
        }

        // Проверяем переполнение по площади
        if (usedSpace + pkg.getWeight() > MAX_CAPACITY) {
            return false;
        }

        // Проверяем коллизии
        if (hasCollision(pkg, x, y)) {
            return false;
        }

        // Проверяем устойчивость (центр тяжести и опору)
        if (!checkStability(pkg, x, y)) {
            return false;
        }

        // Размещаем посылку
        for (int py = 0; py < pkg.getHeight(); py++) {
            String slice = pkg.getSlice(py);
            for (int px = 0; px < slice.length(); px++) {
                if (slice.charAt(px) != ' ') {
                    grid[y + py][x + px] = slice.charAt(px);
                }
            }
        }

        packages.add(pkg);
        usedSpace += pkg.getWeight();
        currentWeight += pkg.getWeight();
        this.packagePositions.put(pkg.getId(), new TruckExportData.Position(x, y));
        return true;
    }

    private boolean hasCollision(Package pkg, int x, int y) {
        for (int py = 0; py < pkg.getHeight(); py++) {
            String slice = pkg.getSlice(py);
            for (int px = 0; px < slice.length(); px++) {
                if (slice.charAt(px) != ' ' && grid[y + py][x + px] != ' ') {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkStability(Package pkg, int x, int y) {
        if (y == 0) return true; // На полу - всегда устойчиво

        // Проверяем опору (не менее 50% площади должно иметь поддержку снизу)
        int supportedBlocks = 0;
        int totalBlocks = 0;

        for (int py = 0; py < pkg.getHeight(); py++) {
            String slice = pkg.getSlice(py);
            for (int px = 0; px < slice.length(); px++) {
                if (slice.charAt(px) != ' ') {
                    totalBlocks++;
                    if (grid[y - 1][x + px] != ' ') {
                        supportedBlocks++;
                    }
                }
            }
        }

        return supportedBlocks >= totalBlocks / 2;
    }

    public Truck copy() {
        Truck copy = new Truck(this.id, this.width, this.height);
        copy.packages.addAll(this.packages);
        copy.currentWeight = this.currentWeight;
        return copy;
    }

    public void clearTrack() {
        this.usedSpace = 0;
        this.currentWeight = 0;
        this.packagePositions.clear();
        initializeGrid();
    }

    public boolean hasPackagePositions() {
        return !packagePositions.isEmpty();
    }

    public Map<String, TruckExportData.Position> getPackagePositions() {
        return Collections.unmodifiableMap(packagePositions);
    }

    // Getters
    public String getId() {return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getUsedSpace() { return usedSpace; }
    public int getCurrentWeight() { return currentWeight; }
    public int getMaxCapacity() { return MAX_CAPACITY; }
    public List<Package> getPackages() { return packages; }
    public boolean isEmpty() { return packages.isEmpty(); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+").append("-".repeat(width)).append("+\n");
        for (char[] row : grid) {
            sb.append("|").append(new String(row)).append("|\n");
        }
        sb.append("+").append("-".repeat(width)).append("+");
        return sb.toString();
    }
}
