package ru.liga.packagefactory.domain.repository;

// PackageRepository.java - репозиторий для работы с посылками
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ru.liga.packagefactory.domain.model.Package;

public class PackageRepository implements PackageRepositoryInterface {

    @Override
    public List<Package> loadPackagesFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<Package> packages = new ArrayList<>();
        List<String> currentPackageLines = new ArrayList<>();

        for (String line : lines) {
            String trimmedLine = line.stripTrailing();

            if (trimmedLine.isEmpty()) {
                if (!currentPackageLines.isEmpty()) {
                    packages.add(createPackage(currentPackageLines));
                    currentPackageLines.clear();
                }
            } else {
                currentPackageLines.add(trimmedLine);
            }
        }

        // Добавляем последнюю посылку, если файл не заканчивается пустой строкой
        if (!currentPackageLines.isEmpty()) {
            packages.add(createPackage(currentPackageLines));
        }

        return packages;
    }

    private Package createPackage(List<String> packageLines) {
        String id = UUID.randomUUID().toString();
        return new Package(id, packageLines);
    }
}
