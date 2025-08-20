package ru.liga.packagefactory.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TruckExportData {
    private final String truckId;
    private final int width;
    private final int height;
    private final int capacity;
    private final int getUsedSpace;
    private List<PackageExportData> packages;

    public TruckExportData(String id, int width, int height, int capacity, int getUsedSpace, List<PackageExportData> packages) {
        this.truckId = id;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.getUsedSpace = getUsedSpace;
        this.packages = packages;
    }

    @Data
    @AllArgsConstructor
    public static class PackageExportData {
        private final String id;
        private final int weight;
        private List<String> slices;
        private final Position position;
    }

    @Data
    @AllArgsConstructor
    public static class Position {
        private final int x; // координата по длине грузовика
        private final int y; // координата по ширине
    }
}
