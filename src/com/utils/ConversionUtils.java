import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConversionUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    public static ZonedDateTime parseDate(String date) {
        return ZonedDateTime(date, DATE_TIME_FORMATTER);
    }

    private static ZonedDateTime ZonedDateTime(String date, DateTimeFormatter dateTimeFormatter) {
        return null;
    }
}