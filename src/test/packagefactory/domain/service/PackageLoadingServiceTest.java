package test.packagefactory.domain.service;

import main.java.ru.liga.packagefactory.domain.model.Truck;
import main.java.ru.liga.packagefactory.domain.service.PackageLoadingService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import main.java.ru.liga.packagefactory.domain.model.Package;

class PackageLoadingServiceTest {
    private final PackageLoadingService service = new PackageLoadingService();

    @Test
    void testTruckDimensions() {
        Truck truck = new Truck();
        assertEquals(6, truck.getWidth());
        assertEquals(6, truck.getHeight());
        assertEquals(36, truck.getMaxCapacity());
    }

    @Test
    void testOptimizedPackWithinCapacity() {
        List<Package> packages = Arrays.asList(
                new Package("1", Arrays.asList("1111", "1111")), // 4x2 (8)
                new Package("2", Arrays.asList("222", "222")),   // 3x2 (6)
                new Package("3", Arrays.asList("33333")),        // 5x1 (5)
                new Package("4", Arrays.asList("44", "44")),     // 2x2 (4)
                new Package("5", Arrays.asList("5", "5")),       // 1x2 (2)
                new Package("6", Arrays.asList("6666")),        // 4x1 (4)
                new Package("7", Arrays.asList("77"))           // 2x1 (2)
        ); // Общий вес: 8+6+5+4+2+4+2 = 31 < 36

        List<Truck> trucks = service.optimizedPack(packages);
        assertEquals(1, trucks.size(), "Все посылки должны поместиться в один грузовик");
        assertEquals(31, trucks.getFirst().getCurrentWeight());
    }

    @Test
    void testExactTruckCapacity() {
        // Посылка точно на весь грузовик
        List<Package> packages = List.of(
                new Package("full", Arrays.asList(
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX"))
        );

        List<Truck> trucks = service.simplePack(packages);
        assertEquals(1, trucks.size());
        assertEquals(36, trucks.getFirst().getCurrentWeight());
    }
}