package packagefactory.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.liga.packagefactory.domain.enums.AlgorithmType;
import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.service.PackageLoadingService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import ru.liga.packagefactory.domain.model.Package;

class PackageLoadingServiceTest {
    private PackageLoadingService service;

    @Test
    void testTruckDimensions() {
        Truck truck = new Truck(UUID.randomUUID().toString());

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
        List<Truck> emptyTrucks = List.of(new Truck("Truck_1", 6, 6));
        List<Package> packages = Arrays.asList(
                new Package("7", Arrays.asList("77")),           // 2x1 (2)
                new Package("2", Arrays.asList("222", "222")),   // 3x2 (6)
                new Package("3", Arrays.asList("33333")),       // 5x1 (5)
                new Package("4", Arrays.asList("44", "44")),    // 2x2 (4)
                new Package("5", Arrays.asList("5", "5")),      // 1x2 (2)
                new Package("1", Arrays.asList("1111", "1111")), // 4x2 (8)
                new Package("6", Arrays.asList("6666"))         // 4x1 (4)
        ); // Общий вес: 8+6+5+4+2+4+2 = 31 < 36

        service = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.AdvancedLoadingAlgorithm());
        List<Truck> trucks = service.pack(emptyTrucks, packages, AlgorithmType.fromString("optimized"));

        assertThat(trucks)
                .as("Все посылки должны поместиться в один грузовик")
                .hasSize(1);

        assertThat(trucks.getFirst().getCurrentWeight())
                .as("Общий вес посылок в грузовике")
                .isEqualTo(31);

        service.printTrucks(trucks);
    }

    @Test
    void testUniformPackWithinCapacity() {
        List<Truck> emptyTrucks = List.of(new Truck("Truck_1", 6, 6),
                new Truck("Truck_2", 6, 6));
        List<Package> packages = Arrays.asList(
                new Package("7", Arrays.asList("77")),           // 2x1 (2)
                new Package("2", Arrays.asList("222", "222")),   // 3x2 (6)
                new Package("3", Arrays.asList("33333")),       // 5x1 (5)
                new Package("4", Arrays.asList("44", "44")),    // 2x2 (4)
                new Package("5", Arrays.asList("5", "5")),      // 1x2 (2)
                new Package("1", Arrays.asList("1111", "1111")), // 4x2 (8)
                new Package("6", Arrays.asList("6666"))         // 4x1 (4)
        ); // Общий вес: 8+6+5+4+2+4+2 = 31 < 36

        service = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.SimpleLoadingAlgorithm());
        List<Truck> trucks = service.pack(emptyTrucks, packages, AlgorithmType.fromString("uniform"));

        assertThat(trucks)
                .as("Все посылки должны погрузиться в два грузовика")
                .hasSize(2);

        int totalWeight = trucks.stream()
                .mapToInt(Truck::getCurrentWeight)
                .sum();

        assertThat(totalWeight)
                .as("Суммарный вес посылок во всех грузовиках")
                .isEqualTo(31);

        service.printTrucks(trucks);
    }

    @Test
    void testSimplePackWithinCapacity() {
        List<Truck> emptyTrucks = List.of(new Truck("Truck_1", 6, 6),
                new Truck("Truck_2", 6, 6));
        List<Package> packages = Arrays.asList(
                new Package("7", Arrays.asList("77")),           // 2x1 (2)
                new Package("2", Arrays.asList("222", "222")),   // 3x2 (6)
                new Package("3", Arrays.asList("33333")),       // 5x1 (5)
                new Package("4", Arrays.asList("44", "44")),    // 2x2 (4)
                new Package("5", Arrays.asList("5", "5")),      // 1x2 (2)
                new Package("1", Arrays.asList("1111", "1111")), // 4x2 (8)
                new Package("6", Arrays.asList("6666"))         // 4x1 (4)
        ); // Общий вес: 8+6+5+4+2+4+2 = 31 < 36

        service = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.SimpleLoadingAlgorithm());
        List<Truck> trucks = service.pack(emptyTrucks, packages, AlgorithmType.fromString("simple"));

        assertThat(trucks)
                .as("Две посылки должны погрузиться в два грузовика")
                .hasSize(2);

        int totalWeight = trucks.stream()
                .mapToInt(Truck::getCurrentWeight)
                .sum();

        assertThat(totalWeight)
                .as("Суммарный вес двух посылок во всех грузовиках")
                .isEqualTo(8);

        service.printTrucks(trucks);
    }

    @Test
    void testExactTruckCapacity() {
        // Посылка точно на весь грузовик
        List<Truck> emptyTrucks = List.of(new Truck("Truck_1", 6, 6));
        List<Package> packages = List.of(
                new Package("full", Arrays.asList(
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX",
                        "XXXXXX"))
        );

        service = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.SimpleLoadingAlgorithm());
        List<Truck> trucks = service.pack(emptyTrucks, packages, AlgorithmType.fromString("simple"));

        assertThat(trucks)
                .as("Должен быть использован ровно один грузовик")
                .hasSize(1);

        assertThat(trucks.getFirst().getCurrentWeight())
                .as("Грузовик должен быть заполнен полностью")
                .isEqualTo(36);
    }
}