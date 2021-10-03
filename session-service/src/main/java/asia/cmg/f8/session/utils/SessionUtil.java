package asia.cmg.f8.session.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import com.google.gson.Gson;

public class SessionUtil {

	private static final Gson gson = new Gson();
	
	public static LocalDate convertToLocalDate(long timeInMilliSecond) {
		LocalDate date = Instant.ofEpochMilli(timeInMilliSecond).atZone(ZoneId.systemDefault()).toLocalDate();
		return date;
	}
	
	public static LocalDateTime convertToLocalDateTime(long timeInMilliSecond) {
		LocalDateTime date = Instant.ofEpochMilli(timeInMilliSecond).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return date;
	}
	
	public static long convertDateTimeToEpochMiliSecond(LocalDateTime localDateTime) {
		long miliSecond = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return miliSecond;
	}
	
	public static long convertDateTimeToEpochSecond(LocalDateTime localDateTime) {
		long miliSecond = localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
		return miliSecond;
	}
	
	public static long convertDateToEpochMiliSecond(LocalDate localTime) {
		long miliSecond = localTime.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return miliSecond;
	}
	
	public static LocalDate getCurrentDate() {
		return LocalDate.now();
	}
	
	public static long getCurrentDateInMillis() {
		return LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static String toJsonString(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * Convert from json string to Object
	 * @param json string
	 * @param clazz
	 * @return null if exception occurred
	 */
	public static Object toObjectList(String json, Class<?> clazz) {
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
