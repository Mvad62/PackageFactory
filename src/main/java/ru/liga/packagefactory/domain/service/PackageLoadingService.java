package ru.liga.packagefactory.domain.service;

import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.model.Package;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class PackageLoadingService {
    private static final int TRUCK_WIDTH = 6;
    private static final int TRUCK_HEIGHT = 6;

    public List<Truck> simplePack(List<Package> packages) {
        List<Truck> trucks = new ArrayList<>();

        for (Package pkg : packages) {
            Truck truck = new Truck(TRUCK_WIDTH, TRUCK_HEIGHT);
            if (tryPlacePackageEverywhere(truck, pkg)) {
                trucks.add(truck);
            } else {
                System.err.printf("Error: Cannot place package %s (size %dx%d) in truck%n",
                        pkg.getId(), pkg.getMaxWidth(), pkg.getHeight());
            }
        }

        return trucks;
    }

    public List<Truck> optimizedPack(List<Package> packages) {
        List<Truck> trucks = new ArrayList<>();
        if (packages.isEmpty()) return trucks;

        // Сортируем по убыванию площади (ширина*высота), затем по весу
        List<Package> sortedPackages = new ArrayList<>(packages);
        sortedPackages.sort(Comparator
                .comparingInt((Package p) -> p.getMaxWidth() * p.getHeight())
                .reversed()
                .thenComparing(Comparator.comparingInt(Package::getWeight).reversed())
        );

        Truck currentTruck = new Truck(TRUCK_WIDTH, TRUCK_HEIGHT);

        for (Package pkg : sortedPackages) {
            boolean placed = tryPlaceInExistingTrucks(trucks, pkg);
            log.debug("Посылка {} {}", pkg.getId(), placed);

            if (!placed) {
                placed = tryPlaceInCurrentTruck(currentTruck, pkg);
                log.debug("Посылка {} {}", pkg.getId(), placed);

                if (!placed) {
                    trucks.add(currentTruck);
                    currentTruck = new Truck(TRUCK_WIDTH, TRUCK_HEIGHT);
                    placed = tryPlaceInCurrentTruck(currentTruck, pkg);
                    log.debug("Посылка {} {}", pkg.getId(), placed);

                    if (!placed) {
                        System.err.printf("Error: Package %s (size %dx%d) is too large for empty truck%n",
                                pkg.getId(), pkg.getMaxWidth(), pkg.getHeight());
                    }
                }
            }
        }

        if (!currentTruck.isEmpty()) {
            trucks.add(currentTruck);
        }

        return trucks;
    }

    private boolean tryPlaceInExistingTrucks(List<Truck> trucks, Package pkg) {
        for (Truck truck : trucks) {
            if (tryPlacePackageEverywhere(truck, pkg)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceInCurrentTruck(Truck truck, Package pkg) {
        return tryPlacePackageEverywhere(truck, pkg);
    }

    private boolean tryPlacePackageEverywhere(Truck truck, Package pkg) {
        // Пробуем все возможные позиции
        for (int y = 0; y <= TRUCK_HEIGHT - pkg.getHeight(); y++) {
            for (int x = 0; x <= TRUCK_WIDTH - pkg.getMaxWidth(); x++) {
                if (truck.placePackage(pkg, x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void printTrucks(List<Truck> trucks) {
        System.out.printf("%n=== Packing result (%d trucks) ===%n", trucks.size());
        for (int i = 0; i < trucks.size(); i++) {
            Truck truck = trucks.get(i);
            System.out.printf("%nTruck #%d (Weight: %d/%d):%n%s%n",
                    i + 1,
                    truck.getCurrentWeight(),
                    truck.getMaxCapacity(),
                    truck.toString());
        }
    }
}
