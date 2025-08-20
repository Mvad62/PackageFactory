package ru.liga.packagefactory.domain.service.util;

import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.model.Package;
import ru.liga.packagefactory.domain.model.TruckExportData;
import ru.liga.packagefactory.domain.service.PackageLoadingService.LoadingAlgorithm.LoadingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PackagePlacer {
    private PackagePlacer() {
        // Запрещаем создание экземпляров
    }

    public static LoadingResult tryPlacePackage(Truck truck, Package pkg) {
        // Пробуем все возможные позиции
        for (int y = 0; y <= truck.getHeight() - pkg.getHeight(); y++) {
            for (int x = 0; x <= truck.getWidth() - pkg.getMaxWidth(); x++) {
                if (truck.placePackage(pkg, x, y)) {
                    Map<String, TruckExportData.Position> positions = new HashMap<>();
                    positions.put(pkg.getId(), new TruckExportData.Position(x, y));
                    return new LoadingResult(true, positions);
                }
            }
        }
        return new LoadingResult(false, Collections.emptyMap());
    }
}
