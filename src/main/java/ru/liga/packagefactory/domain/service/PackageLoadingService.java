package ru.liga.packagefactory.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.enums.AlgorithmType;
import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.model.Package;
import ru.liga.packagefactory.domain.model.TruckExportData;
import ru.liga.packagefactory.domain.service.util.PackagePlacer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PackageLoadingService {
    private static final int TRUCK_WIDTH = 6;
    private static final int TRUCK_HEIGHT = 6;
    private final ObjectMapper objectMapper;
    private final LoadingAlgorithm loadingAlgorithm;

    public PackageLoadingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.loadingAlgorithm = new SimpleLoadingAlgorithm();
    }

    public PackageLoadingService(ObjectMapper objectMapper, LoadingAlgorithm loadingAlgorithm) {
        this.objectMapper = objectMapper;
        this.loadingAlgorithm = loadingAlgorithm;
    }

    public List<Truck> pack(List<Truck> trucks, List<Package> packages, AlgorithmType algorithmType) {
        if (packages.isEmpty()) return trucks;
        // Создаем копии, чтобы не изменять оригинальные данные
        LoadingAlgorithm algorithm = null;
        boolean uniform = false;
        int maxTrucksPackages = trucks.size() * packages.size();
        int currentTrucksPackages = 0;
        List<Truck> availableTrucks = new ArrayList<>(trucks);
        List<Package> remainingPackages = new ArrayList<>(packages);

        switch (algorithmType) {
            case OPTIMIZED:
                // Для оптимизированного алгоритма сортируем посылки
                algorithm = new AdvancedLoadingAlgorithm();
                remainingPackages.sort(Comparator
                        .comparingInt((Package p) -> p.getMaxWidth() * p.getHeight())
                        .reversed()
                        .thenComparing(Comparator.comparingInt(Package::getWeight).reversed())
                );
                break;
            case UNIFORM:
                algorithm = new AdvancedLoadingAlgorithm();
                remainingPackages.sort(Comparator
                        .comparingInt((Package p) -> p.getMaxWidth() * p.getHeight())
                        .reversed()
                        .thenComparing(Comparator.comparingInt(Package::getWeight).reversed())
                );
                uniform = true;
                algorithm = new SimpleLoadingAlgorithm();
                break;
            case SIMPLE:
                algorithm = new SimpleLoadingAlgorithm();
                break;
        }

        int truckIndex = 0;

        while (!remainingPackages.isEmpty() && (uniform || truckIndex < availableTrucks.size())) {
            Truck currentTruck = availableTrucks.get(truckIndex);

            // Пробуем загрузить оставшиеся посылки в текущий грузовик
            LoadingAlgorithm.LoadingResult result = algorithm.load(currentTruck, remainingPackages);

            // Удаляем успешно загруженные посылки
            for (String loadedPackageId : result.getPositions().keySet()) {
                remainingPackages.removeIf(pkg -> pkg.getId().equals(loadedPackageId));
            }

            truckIndex++;
            if(uniform && truckIndex >= availableTrucks.size()) {
                truckIndex = 0;
                currentTrucksPackages++;
                if(currentTrucksPackages > maxTrucksPackages) {
                    break;
                }
            }

        }

        if (!remainingPackages.isEmpty()) {
            log.error("Не удалось разместить {} посылок из {}", remainingPackages.size(), packages.size());
        }

        return availableTrucks;
    }

    private Truck createNewTruck() {
        return new Truck(UUID.randomUUID().toString(), TRUCK_WIDTH, TRUCK_HEIGHT);
    }

    public void printTrucks(List<Truck> trucks) {
        System.out.printf("%n=== Результат погрузки (%d грузовиков) ===%n", trucks.size());
        for (int i = 0; i < trucks.size(); i++) {
            Truck truck = trucks.get(i);
            System.out.printf("%nГрузовик #%s (Вес: %d/%d):%n%s%n",
                    truck.getId(),
                    truck.getCurrentWeight(),
                    truck.getMaxCapacity(),
                    truck.toString());
        }
    }

    public String exportLoadedTrucksToJson(List<Truck> loadedTrucks, String filePath) throws IOException {
        List<TruckExportData> exportData = loadedTrucks.stream()
                .map(this::convertToExportData)  // Добавляем преобразование через convertToExportData
                .collect(Collectors.toList());

        ExportResult result = new ExportResult(
                loadedTrucks.size(),
                exportData
        );

        if (filePath != null && !filePath.isEmpty()) {
            // Создаем родительские директории, если нужно
            File outputFile = new File(filePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, result);
            return "Данные успешно экспортированы в файл: " + outputFile.getAbsolutePath();
        } else {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        }
    }

protected TruckExportData convertToExportData(Truck truck) {
    Map<String, TruckExportData.Position> positions;

    if (truck.hasPackagePositions()) {
        positions = new HashMap<>(truck.getPackagePositions());
    } else {
        LoadingAlgorithm.LoadingResult result = loadingAlgorithm.load(truck, truck.getPackages());
        positions = result.getPositions();
    }

    List<TruckExportData.PackageExportData> packageData = truck.getPackages().stream()
            .map(pkg -> new TruckExportData.PackageExportData(
                    pkg.getId(),
                    pkg.getWeight(),
                    pkg.getSlices(),
                    positions.get(pkg.getId())
            ))
             .collect(Collectors.toList());

    return new TruckExportData(
            truck.getId(),
            truck.getWidth(),
            truck.getHeight(),
            truck.getMaxCapacity(),
            truck.getUsedSpace(),
            packageData
    );
}

    @Data
    @AllArgsConstructor
    private static class ExportResult {
        private int loadedTrucksCount;
        private List<TruckExportData> loadedTrucks;
    }

    public interface LoadingAlgorithm {
        LoadingResult load(Truck truck, List<Package> packages);

        @Data
        @AllArgsConstructor
        class LoadingResult {
            private boolean success;
            private Map<String, TruckExportData.Position> positions;
        }
    }

    public static class SimpleLoadingAlgorithm implements LoadingAlgorithm {
        @Override
        public LoadingResult load(Truck truck, List<Package> packages) {
            Map<String, TruckExportData.Position> positions = new HashMap<>();
            if(packages.isEmpty()) {
                return new LoadingResult(false, positions);
            }
            // Создаем копию списка для безопасной итерации
            List<Package> packagesCopy = new ArrayList<>(packages);
                LoadingResult result = PackagePlacer.tryPlacePackage(truck, packagesCopy.getFirst());
                if (!result.isSuccess()) {
                    return new LoadingResult(false, positions);
                }
                positions.putAll(result.getPositions());
            return new LoadingResult(true, positions);
        }
    }

    public static class AdvancedLoadingAlgorithm implements LoadingAlgorithm {
        @Override
        public LoadingResult load(Truck truck, List<Package> packages) {
            Map<String, TruckExportData.Position> positions = new HashMap<>();
            if(packages.isEmpty()) {
                return new LoadingResult(false, positions);
            }
            List<Package> packagesCopy = new ArrayList<>(packages);
            for (Package pkg : packagesCopy) {
                LoadingResult result = PackagePlacer.tryPlacePackage(truck, pkg);
                if (result.isSuccess()) {
                    log.debug("Посылка {} размещена в грузовик {}", pkg.getId(), truck.getId());
                }
                else {
                    log.debug("Посылка {} не размещена в грузовик {}", pkg.getId(), truck.getId());
                    return new LoadingResult(false, positions);
                }
                positions.putAll(result.getPositions());
            }
            return new LoadingResult(true, positions);
        }
    }
}
