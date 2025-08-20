package ru.liga.packagefactory.domain.enums;
import lombok.Getter;

@Getter
public enum AlgorithmType {
    SIMPLE("simple"),
    OPTIMIZED("optimized"),
    UNIFORM("uniform");

    private final String value;

    AlgorithmType(String value) {
        this.value = value;
    }

    public static AlgorithmType fromString(String value) {
        for (AlgorithmType type : AlgorithmType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Неизвестный алгоритм: " + value +
                ". Доступные значения: simple, optimized, uniform");
    }
}
