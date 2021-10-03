package asia.cmg.f8.notification.util;

public class CommonStringUtils {

	public static String formatNameInNotification(String account_name) {
		return String.format("[%s]", account_name);
	}
}
