package packagefactory.domain.service;

import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.service.PackageLoadingService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import ru.liga.packagefactory.domain.model.Package;

class PackageLoadingServiceTest {
    private final PackageLoadingService service = new PackageLoadingService();

    @Test
    void testTruckDimensions() {
        Truck truck = new Truck();

        assertThat(truck.getWidth())
                .as("Ширина грузовика")
                .isEqualTo(6);

        assertThat(truck.getHeight())
                .as("Высота грузовика")
                .isEqualTo(6);

        assertThat(truck.getMaxCapacity())
                .as("Максимальная вместимость грузовика")
                .isEqualTo(36);
    }

    @Test
    void testOptimizedPackWithinCapacity() {
        List<Package> packages = Arrays.asList(
                new Package("7", Arrays.asList("77")),           // 2x1 (2)
                new Package("2", Arrays.asList("222", "222")),   // 3x2 (6)
                new Package("3", Arrays.asList("33333")),       // 5x1 (5)
                new Package("4", Arrays.asList("44", "44")),    // 2x2 (4)
                new Package("5", Arrays.asList("5", "5")),      // 1x2 (2)
                new Package("1", Arrays.asList("1111", "1111")), // 4x2 (8)
                new Package("6", Arrays.asList("6666"))         // 4x1 (4)
        ); // Общий вес: 8+6+5+4+2+4+2 = 31 < 36

        List<Truck> trucks = service.optimizedPack(packages);

        assertThat(trucks)
                .as("Все посылки должны поместиться в один грузовик")
                .hasSize(1);

        assertThat(trucks.getFirst().getCurrentWeight())
                .as("Общий вес посылок в грузовике")
                .isEqualTo(31);

        service.printTrucks(trucks);
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

        assertThat(trucks)
                .as("Должен быть использован ровно один грузовик")
                .hasSize(1);

        assertThat(trucks.getFirst().getCurrentWeight())
                .as("Грузовик должен быть заполнен полностью")
                .isEqualTo(36);
    }
}