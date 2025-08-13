package ru.liga.packagefactory.domain.repository;

// PackageRepository.java - репозиторий для работы с посылками

import ru.liga.packagefactory.domain.model.Package;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class PackageRepository implements PackageRepositoryInterface {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Package> loadPackagesFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<Package> packages = new ArrayList<>();
        List<String> currentPackageLines = new ArrayList<>();

        for (String line : lines) {
            String trimmedLine = line.stripTrailing();

            if (trimmedLine.isEmpty()) {
                if (!currentPackageLines.isEmpty()) {
                    packages.add(createPackage(new ArrayList<>(currentPackageLines)));
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

    @Override
    public List<Package> loadPackagesFromJSONFile(String filePath) throws IOException {
        List<PackageJsonModel> jsonPackages = objectMapper.readValue(
                Paths.get(filePath).toFile(),
                new TypeReference<>() {}
        );

        List<Package> packages = new ArrayList<>();
        for (PackageJsonModel jsonPkg : jsonPackages) {
            packages.add(createPackageFromJson(jsonPkg));
        }
        return packages;
    }

    private Package createPackageFromJson(PackageJsonModel jsonPkg) {
        String id = jsonPkg.getId() != null ? jsonPkg.getId() : UUID.randomUUID().toString();
        return new Package(id, jsonPkg.getSlices());
    }

    // Модель для десериализации JSON
    private static class PackageJsonModel {
        private String id;
        private List<String> slices;

        // Геттеры и сеттеры
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public List<String> getSlices() { return slices; }
        public void setSlices(List<String> slices) { this.slices = slices; }
    }
}
