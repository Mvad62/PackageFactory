package test.packagefactory.domain.repository;

import main.java.ru.liga.packagefactory.domain.model.Package;
import main.java.ru.liga.packagefactory.domain.repository.PackageRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PackageRepositoryTest {
    @Test
    void testLoadPackagesFromFile() throws IOException {
        // Подготовка тестовых данных
        String testContent = """
        777
         777
          77
            
        999
        9999
         999
            
         11
        11
        """;

        Path testFile = Files.createTempFile("test-packages", ".txt");
        Files.writeString(testFile, testContent);

        // Тестируемый объект
        PackageRepository repository = new PackageRepository();

        // Выполнение
        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());

        // Проверки
        assertEquals(3, packages.size(), "Should load 3 packages");

        // Проверка первой посылки (пирамида из 7)
        Package pkg1 = packages.getFirst();
        assertEquals(3, pkg1.getHeight(), "First package height");
        assertEquals(4, pkg1.getMaxWidth(), "First package max width");
        assertEquals(8, pkg1.getWeight(), "First package weight");
        assertEquals(1.57, pkg1.getCenterOfMassX(), 0.01, "First package center of mass X");

        // Проверка второй посылки (блок из 9)
        Package pkg2 = packages.get(1);
        assertEquals(3, pkg2.getHeight(), "Second package height");
        assertEquals(4, pkg2.getMaxWidth(), "Second package max width");
        assertEquals(10, pkg2.getWeight(), "Second package weight");

        // Проверка третьей посылки (блок из 1)
        Package pkg3 = packages.get(2);
        assertEquals(2, pkg3.getHeight(), "Third package height");
        assertEquals(2, pkg3.getMaxWidth(), "Third package max width");
        assertEquals(4, pkg3.getWeight(), "Third package weight");

        // Уборка
        Files.delete(testFile);
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        Path testFile = Files.createTempFile("test-empty", ".txt");
        PackageRepository repository = new PackageRepository();

        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());

        assertTrue(packages.isEmpty(), "Should return empty list for empty file");
        Files.delete(testFile);
    }

    @Test
    void testLoadFileWithOnlyEmptyLines() throws IOException {
        String testContent = "\n\n\n";
        Path testFile = Files.createTempFile("test-empty-lines", ".txt");
        Files.writeString(testFile, testContent);

        PackageRepository repository = new PackageRepository();
        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());

        assertTrue(packages.isEmpty(), "Should ignore empty lines");
        Files.delete(testFile);
    }
}
