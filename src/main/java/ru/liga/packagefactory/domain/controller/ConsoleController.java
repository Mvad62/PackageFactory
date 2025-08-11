package ru.liga.packagefactory.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.model.Package;
import ru.liga.packagefactory.domain.model.Truck;
import ru.liga.packagefactory.domain.repository.PackageRepository;
import ru.liga.packagefactory.domain.service.PackageLoadingService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ConsoleController {

    private final PackageLoadingService loadingService;
    private final PackageRepository repository;
    private final Pattern IMPORT_COMMAND_PATTERN = Pattern.compile("import (.+\\.csv)");

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
                String filePath = matcher.group(1);
                try {
                    //PackageRepository repository = new PackageRepository();
                    //PackageLoadingService loadingService = new PackageLoadingService();

                    List<Package> packages = repository.loadPackagesFromFile(filePath);

                    System.out.println("Simple packing (one package per truck):");
                    List<Truck> simpleTrucks = loadingService.simplePack(packages);
                    loadingService.printTrucks(simpleTrucks);

                    System.out.println("\nOptimized packing:");
                    List<Truck> optimizedTrucks = loadingService.optimizedPack(packages);
                    loadingService.printTrucks(optimizedTrucks);

                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid package data: " + e.getMessage());
                }

            }
            else {
                log.error("Недопустимая команда: {}", command);
            }
        }
    }

}
