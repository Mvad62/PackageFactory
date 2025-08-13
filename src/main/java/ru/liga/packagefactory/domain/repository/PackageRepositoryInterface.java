package ru.liga.packagefactory.domain.repository;

import ru.liga.packagefactory.domain.model.Package;
import java.io.IOException;
import java.util.List;

public interface PackageRepositoryInterface {
    List<Package> loadPackagesFromFile(String filePath) throws IOException;
    List<Package> loadPackagesFromJSONFile(String filePath) throws IOException;
}
