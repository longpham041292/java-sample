package asia.cmg.f8.session.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.ReservationSlot;
import asia.cmg.f8.session.dto.TimeSlot;

/**
 * Created on 12/5/16.
 */
public final class TimeSlotUtil {

    private TimeSlotUtil() {
    }

    public static Set<ReservationSlot> parseTimeSlot(final Set<TimeSlot> timeSlots) {
        return timeSlots.stream().map(timeSlot -> {
            final ReservationSlot reservationSlot = new ReservationSlot();
            reservationSlot.setStartTime(ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeSlot.getStartTime()));
            reservationSlot.setEndTime(ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeSlot.getEndTime()));
            reservationSlot.setConfirmed(timeSlot.getConfirmed());
            final String sessionId = timeSlot.getSessionId();
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                reservationSlot.setSessionId(sessionId);
            } else {
                reservationSlot.setSessionId(null);
            }
            return reservationSlot;
        }).collect(Collectors.toSet());
    }

    public static long estimateExpireDate(final Timestamp startTime, final int limitDays) {
        return startTime.toLocalDateTime().plusDays(limitDays).withHour(23).withMinute(59)
                .withSecond(59).atZone(ZoneOffset.systemDefault()).toEpochSecond();
    }

    /**
     * Round minute to 0 or 30. Reset second, nano field.
     * 
     * @param input
     * @return
     */
    public static LocalDateTime roundToSlotTime(final LocalDateTime input) {
        final LocalDateTime roundTime = input.withSecond(0).withNano(0);
        final int redundantMin = roundTime.getMinute() % 30;
        if (redundantMin != 0) {
            return roundTime.plusMinutes(30 - redundantMin);
        }
        return roundTime;
    }

}
