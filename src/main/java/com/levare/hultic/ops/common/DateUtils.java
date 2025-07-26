package com.levare.hultic.ops.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Утилитные методы для работы с датами и временем.
 */
public final class DateUtils {

    private DateUtils() {
        // приватный конструктор — класс только для статических методов
    }

    /**
     * Преобразует миллисекунды от эпохи (Unix time) в объект LocalDate
     * в зоне по умолчанию.
     *
     * @param epochMilli время в миллисекундах от 1970-01-01T00:00:00Z
     * @return LocalDate в системной временной зоне
     */
    public static LocalDate epochToLocalDate(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
