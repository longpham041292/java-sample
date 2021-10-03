package asia.cmg.f8.common.util;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.cmg.f8.common.exception.InvalidTimeRangeException;
import asia.cmg.f8.common.spec.report.TimeInfo;
import asia.cmg.f8.common.spec.report.TimeRange;

/**
 * Created on 11/24/16.
 */
public final class DateUtils {
	
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    private DateUtils() {
        
    }
    
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static Date asDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static LocalDateTime asLocalDateTime(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static String parseDateToString(final Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(date);
    }
    
    public static TimeInfo calculateTimeRange(final TimeRange timeRange) {
        long startTime = 0;
        long endTime = 0;
        long middleTime = 0;

        switch (timeRange) {
        case WEEK:
            startTime = ZoneDateTimeUtils.getStartTimeOfDate(CommonConstant.LAST_TWO_WEEKS);
            middleTime = ZoneDateTimeUtils.getStartTimeOfDate(CommonConstant.LAST_WEEK);
            endTime = ZoneDateTimeUtils.getEndTimeOfDate(CommonConstant.YESTERDAY);
            break;
        case MONTH:
            startTime = ZoneDateTimeUtils.getStartTimeOfDate(CommonConstant.LAST_TWO_MONTHS);
            middleTime = ZoneDateTimeUtils.getStartTimeOfDate(CommonConstant.LAST_MONTH);
            endTime = ZoneDateTimeUtils.getEndTimeOfDate(CommonConstant.YESTERDAY);
            break;
        case YEAR:
            startTime = ZoneDateTimeUtils.getStartTimeOfMonth(CommonConstant.LAST_TWO_YEARS);
            middleTime = ZoneDateTimeUtils.getStartTimeOfMonth(CommonConstant.LAST_YEAR);
            endTime = ZoneDateTimeUtils.getEndTimeOfDate(CommonConstant.YESTERDAY);
            break;    
        default:
            break;
        }

        if (startTime == 0 || endTime == 0 || middleTime == 0) {
            throw new InvalidTimeRangeException(String.format("Unsupport time range %s", timeRange));
        }

        LOG.info("Start time {}, middle time {}, end time {}", startTime, middleTime, endTime);
        return new TimeInfo(startTime, middleTime, endTime);
    }
    
    public static TimeInfo calculateLocalTimeRange(final TimeRange timeRange) {
        long startTime = 0;
        long endTime = 0;
        long middleTime = 0;

		switch (timeRange) {
		case WEEK:
			startTime = ZoneDateTimeUtils.getLocalStartTimeOfDate(CommonConstant.LAST_TWO_WEEKS);
			middleTime = ZoneDateTimeUtils.getLocalStartTimeOfDate(CommonConstant.LAST_WEEK);
			endTime = ZoneDateTimeUtils.getLocalEndTimeOfDate(CommonConstant.YESTERDAY);
			break;
		case MONTH:
			startTime = ZoneDateTimeUtils.getLocalStartTimeOfDate(CommonConstant.LAST_TWO_MONTHS);
			middleTime = ZoneDateTimeUtils.getLocalStartTimeOfDate(CommonConstant.LAST_MONTH);
			endTime = ZoneDateTimeUtils.getLocalEndTimeOfDate(CommonConstant.YESTERDAY);
			break;
		case YEAR:
			startTime = ZoneDateTimeUtils.getLocalStartTimeOfMonth(CommonConstant.LAST_TWO_YEARS);
			middleTime = ZoneDateTimeUtils.getLocalStartTimeOfMonth(CommonConstant.LAST_YEAR);
			endTime = ZoneDateTimeUtils.getLocalEndTimeOfDate(CommonConstant.YESTERDAY);
			break;	
		default:
			break;
		}

        if (startTime == 0 || endTime == 0 || middleTime == 0) {
            throw new InvalidTimeRangeException(String.format("Unsupport time range %s", timeRange));
        }

        LOG.info("Start time {}, middle time {}, end time {}", startTime, middleTime, endTime);
        return new TimeInfo(startTime, middleTime, endTime);
    }
    
    
	public static String formatDateTime(final Long milisec, final String format) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return milisec != null ? ZoneDateTimeUtils
				.convertFromUTCToLocalDateTime(milisec / 1000)
				.format(formatter) : "";
	}
	
	public static long getStartTimeOfWeek(final int weeks) {
		final LocalDate date = LocalDate.now().minusWeeks(weeks)
				.with(DayOfWeek.MONDAY);
		return date.atTime(LocalTime.MIN).toEpochSecond(
				ZoneDateTimeUtils.getCurrentZoneOffset());
	}

	public static long getEndTimeOfWeek(final int weeks) {
		final LocalDate date = LocalDate.now().minusWeeks(weeks)
				.with(DayOfWeek.SUNDAY);
		return date.atTime(LocalTime.MAX).toEpochSecond(
				ZoneDateTimeUtils.getCurrentZoneOffset());
	}
}
