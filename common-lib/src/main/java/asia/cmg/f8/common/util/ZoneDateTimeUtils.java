package asia.cmg.f8.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public final class ZoneDateTimeUtils {

    private ZoneDateTimeUtils() {
    }

    public static ZoneOffset getCurrentZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.now());
    }

    public static long convertToSecondUTC(final LocalDateTime time) {
        return time.atZone(ZoneOffset.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toEpochSecond();
    }

    public static LocalDateTime convertFromUTCToLocalDateTime(final long seconds) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(seconds), ZoneId.systemDefault());
    }

    public static long getStartTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long getEndTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long getStartTimeOfMonth(final int months) {
        final LocalDate date = LocalDate.now().plusMonths(months).withDayOfMonth(1);
        return date.atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    /**
     * Get start time of day in second in current time zone.
     *
     * @param days plus days
     * @return seconds
     */
    public static long getLocalStartTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MIN).toEpochSecond(getCurrentZoneOffset());
    }

    /**
     * Get end time of day in second in current time zone.
     *
     * @param days plus days
     * @return seconds
     */
    public static long getLocalEndTimeOfDate(final int days) {
        final LocalDate date = LocalDate.now().plusDays(days);
        return date.atTime(LocalTime.MAX).toEpochSecond(getCurrentZoneOffset());
    }

    public static long getLocalStartTimeOfMonth(final int months) {
        final LocalDate date = LocalDate.now().plusMonths(months).withDayOfMonth(1);
        return date.atTime(LocalTime.MIN).toEpochSecond(getCurrentZoneOffset());
    }
}
