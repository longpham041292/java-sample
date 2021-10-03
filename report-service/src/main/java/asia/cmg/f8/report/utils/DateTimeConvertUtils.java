package asia.cmg.f8.report.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public final class DateTimeConvertUtils {

    private DateTimeConvertUtils() {
    }

    public static long getStartTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long getEndTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
