package asia.cmg.f8.notification.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtils {
    
    private DateTimeUtils (){
        
    }
    
    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static String formatDate(final LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    public static String formatDatetime(final LocalDateTime date) {
        return DATETIME_FORMATTER.format(date);
    }

    public static LocalDate convertToLocalDate(long timeInMilliSecond) {
        LocalDate date = Instant.ofEpochMilli(timeInMilliSecond).atZone(ZoneId.systemDefault()).toLocalDate();
        return date;
    }

    public static LocalDateTime convertToLocalDateTime(long timeInMilliSecond) {
        return Instant.ofEpochMilli(timeInMilliSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
