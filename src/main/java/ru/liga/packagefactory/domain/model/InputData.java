package ru.liga.packagefactory.domain.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class InputData {
    private List<Truck> trucks;
    private List<Package> packages;
    private List<Truck> loadedTrucks;

    public List<Truck> getTrucks() {
        return Collections.unmodifiableList(trucks);
    }

    public List<Package> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public List<Truck> getLoadedTrucks() {
        return Collections.unmodifiableList(loadedTrucks);
    }
}


