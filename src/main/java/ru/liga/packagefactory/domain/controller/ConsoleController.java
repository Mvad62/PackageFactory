package ru.liga.packagefactory.domain.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.enums.AlgorithmType;
import ru.liga.packagefactory.domain.model.InputData;
import ru.liga.packagefactory.domain.model.Package;
import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.repository.PackageRepository;
import ru.liga.packagefactory.domain.service.PackageLoadingService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ConsoleController {
    private final PackageRepository repository;
    private final JsonValidator validator;
    private final Pattern IMPORT_COMMAND_PATTERN = Pattern.compile("import (.+\\.txt) (\\w+)");

    public void listen() {
        var scanner = new Scanner(System.in);

        while(scanner.hasNextLine()){
            String command = scanner.nextLine();
            if (command.equals("exit")) {
                System.out.println("Работа закончена");
                System.exit(0);
            }

            Matcher matcher = IMPORT_COMMAND_PATTERN.matcher(command);
            if (matcher.matches()) {
                PackageLoadingService loadingService;
                String filePath = matcher.group(1);
                String algorithmName = matcher.group(2);
                try {
                    List<Truck> trucks = new ArrayList<>();
                    List<Package> packages = new ArrayList<>();
                    List<Truck> inputTrucks = new ArrayList<>();
                    String content = Files.readString(Paths.get(filePath));
                    // Загрузка схемы из ресурсов
                    InputStream schemaStream = getClass().getResourceAsStream("/schemas/truck-loaded-package.json");

                    if (validator.validateTrucksPackagesJson(content, schemaStream)) {
                        InputData data = repository.loadInputData(filePath);
                        trucks = data.getTrucks();
                        packages = data.getPackages();
                        inputTrucks = data.getLoadedTrucks();
                    }

                    for (Truck truck : inputTrucks) {
                        log.debug("В грузовике {} размещено {} посылок", truck.getId(), truck.getPackages().size());
                    }

                    int loadingPackages = 0;
                    AlgorithmType algorithmType = AlgorithmType.fromString(algorithmName);
                    String outputFilename;
                    List<Truck> loadedTrucks;
                    // Выбор алгоритма в зависимости от переданного параметра
                    outputFilename = switch (algorithmType) {
                        case SIMPLE -> {
                            System.out.println("\nПростая погрузка:");
                            loadingService = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.SimpleLoadingAlgorithm());
                            yield "simpleTrucks.json";
                        }
                        case OPTIMIZED -> {
                            System.out.println("\nОптимальная погрузка:");
                            loadingService = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.AdvancedLoadingAlgorithm());
                            yield "optimizedTrucks.json";
                        }
                        case UNIFORM -> {
                            System.out.println("\nРавномерная погрузка:");
                            loadingService = new PackageLoadingService(new ObjectMapper(), new PackageLoadingService.SimpleLoadingAlgorithm());
                            yield "uniformTrucks.json";
                        }
                        default -> throw new IllegalArgumentException("Неизвестный алгоритм: " + algorithmName);
                    };
                    loadedTrucks = loadingService.pack(trucks, packages, algorithmType);

                    for (Truck loadedTruck : loadedTrucks) {
                        loadingPackages += loadedTruck.getPackages().size();
                    }

                    if(loadingPackages < packages.size()) {
                        log.error("Размещено {} посылок из {}", loadingPackages, packages.size());
                    }
                    loadingService.printTrucks(loadedTrucks);
                    System.out.println(loadingService.exportLoadedTrucksToJson(loadedTrucks, outputFilename));

                } catch (IOException e) {
                    System.out.println("Ошибка чтения файла: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Недопустимые данные посылки: " + e.getMessage());
                }
            }
            else {
                log.error("Недопустимая команда: {}", command);
            }
        }
    }

}
