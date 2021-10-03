package asia.cmg.f8.report.utils;

import asia.cmg.f8.common.spec.report.TimeRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import com.google.gson.Gson;

public final class ReportUtils {
	
	private static final Gson gson = new Gson();

    private ReportUtils() {
    }

    public static String getSessionCounterName(final String status) {
        return new StringBuilder().append(ReportConstant.COUNTER_PREFIX)
                .append(status.toLowerCase(Locale.US)).append(ReportConstant.COUNTER_SUFFIX)
                .toString();
    }

    public static String resolveSessionStatus(final String counter) {
        final StringBuilder statusSb = new StringBuilder(counter);
        replace(ReportConstant.COUNTER_PREFIX, "", statusSb);
        replace(ReportConstant.COUNTER_SUFFIX, "", statusSb);
        return statusSb.toString();
    }

    private static void replace(final String target, final String replacement,
                                final StringBuilder builder) {
        final int index;
        if ((index = builder.indexOf(target)) >= 0) {
            builder.replace(index, index + target.length(), replacement);
        }
    }

    /**
     * Only support Day for query week/month.
     *
     * @param timeRange the time range
     * @return new time range
     */
    public static TimeRange getQueryTimeRange(final TimeRange timeRange) {
        if (TimeRange.WEEK.equals(timeRange) || TimeRange.MONTH.equals(timeRange)
                || TimeRange.YEAR.equals(timeRange)) {
            return TimeRange.DAY;
        }
        return timeRange;
    }
    
    public static long convertDateToEpochMiliSecond(LocalDate localTime) {
		long miliSecond = localTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return miliSecond;
	}
    
    public static long convertDateTimeToEpochMiliSecond(LocalDateTime localDateTime) {
		long miliSecond = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return miliSecond;
	}
    
    public static Object toObjectList(String json, Class<?> clazz) {
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception e) {
			return null;
		}
	}
    
    public static long convertDateTimeToEpochSecond(LocalDateTime localDateTime) {
		long miliSecond = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
		return miliSecond;
	}
}
