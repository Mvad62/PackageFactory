package packagefactory.domain.repository;

import ru.liga.packagefactory.domain.model.Package;
import ru.liga.packagefactory.domain.repository.PackageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PackageRepositoryTest {
    @Test
    public void testLoadPackagesFromFile() throws IOException {
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

        PackageRepository repository = new PackageRepository();
        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());

        // Проверки (AssertJ)
        assertThat(packages).hasSize(3);

        Package pkg1 = packages.getFirst();
        assertThat(pkg1.getHeight()).isEqualTo(3);
        assertThat(pkg1.getMaxWidth()).isEqualTo(4);
        assertThat(pkg1.getWeight()).isEqualTo(8);

        Package pkg2 = packages.get(1);
        assertThat(pkg2.getHeight()).isEqualTo(3);
        assertThat(pkg2.getMaxWidth()).isEqualTo(4);
        assertThat(pkg2.getWeight()).isEqualTo(10);

        Package pkg3 = packages.get(2);
        assertThat(pkg3.getHeight()).isEqualTo(2);
        assertThat(pkg3.getMaxWidth()).isEqualTo(3);
        assertThat(pkg3.getWeight()).isEqualTo(4);

        Files.delete(testFile);
    }

    @Test
    public void testLoadEmptyFile() throws IOException {
        Path testFile = Files.createTempFile("test-empty", ".txt");
        PackageRepository repository = new PackageRepository();

        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());
        assertThat(packages).isEmpty();

        Files.delete(testFile);
    }

    @Test
    public void testLoadFileWithOnlyEmptyLines() throws IOException {
        String testContent = "\n\n\n";
        Path testFile = Files.createTempFile("test-empty-lines", ".txt");
        Files.writeString(testFile, testContent);

        PackageRepository repository = new PackageRepository();
        List<Package> packages = repository.loadPackagesFromFile(testFile.toString());

        assertThat(packages).isEmpty();
        Files.delete(testFile);
    }
}