package ru.liga.packagefactory;

import ru.liga.packagefactory.domain.controller.JsonValidator;
import ru.liga.packagefactory.domain.repository.PackageRepository;
import lombok.extern.slf4j.Slf4j;
import ru.liga.packagefactory.domain.controller.ConsoleController;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        log.info("Введите команду:");
        Main.start();
    }

    private static void start() {
        ConsoleController consoleController = new ConsoleController(new PackageRepository(),
                                                                    new JsonValidator());
        consoleController.listen();
    }

}