package ru.liga.packagefactory;

import ru.liga.packagefactory.domain.repository.PackageRepository;
import ru.liga.packagefactory.domain.service.PackageLoadingService;
import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.controller.ConsoleController;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Стартуем приложение...");
        Main.start();
    }

    private static void start() {
        ConsoleController consoleController = new ConsoleController(new PackageLoadingService(),
                                                                    new PackageRepository());
        consoleController.listen();
    }

}