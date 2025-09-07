package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    /**
     * Adjusts a LocalDate by a number of days and returns formatted string.
     *
     * @param baseDate the starting LocalDate
     * @param days     the number of days to add (negative to subtract)
     * @return new date in yyyy-MM-dd format
     */
    public static String adjustDate(LocalDate baseDate, int days, DateTimeFormatter formatter) {
        return baseDate.plusDays(days).format(formatter);
    }

    public static LocalDate adjustDateToLocalDate(LocalDate baseDate, int days) {
        return baseDate.plusDays(days);
    }
}
