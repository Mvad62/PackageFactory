package main.java.ru.liga.packagefactory;

import main.java.ru.liga.packagefactory.domain.model.Package;
import main.java.ru.liga.packagefactory.domain.model.Truck;
import main.java.ru.liga.packagefactory.domain.repository.PackageRepository;
import main.java.ru.liga.packagefactory.domain.service.PackageLoadingService;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main <input_file>");
            return;
        }

        try {
            PackageRepository repository = new PackageRepository();
            PackageLoadingService loadingService = new PackageLoadingService();

            List<Package> packages = repository.loadPackagesFromFile(args[0]);

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
}