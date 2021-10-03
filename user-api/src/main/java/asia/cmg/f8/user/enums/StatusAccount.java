package asia.cmg.f8.user.enums;

import java.util.HashMap;
import java.util.Map;

public enum StatusAccount {
	/** PENDING */
	PENDING(0),
	/** ACTIVATED */
	ACTIVATED(1),
	/** DEACTIVATED */
	DEACTIVATED(2);

	private int order;

	StatusAccount(int order) {
		this.order = order;
	}

	public int order() {
		return this.order;
	}

	public static boolean checkExist(int order) {
		return (order > -1 && order < 4);
	}

	public static boolean checkExist(String name) {
		try {
			valueOf(name);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static String getName(int order) {
		switch (order) {
		case 0:
			return "PENDING";
		case 1:
			return "ACTIVATED";
		case 2:
			return "DEACTIVATED";
		default:
			return "";
		}
	}

	public static StatusAccount getValueOf(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Map<String, Integer> buildMapStatusAccountName() {
		Map<String, Integer> mCount = new HashMap<String, Integer>();
		StatusAccount[] arrEnum = StatusAccount.values();
		for (StatusAccount status : arrEnum) {
			mCount.put(status.name(), 0);
		}
		return mCount;
	}
	
	public static Map<Integer, Integer> buildMapStatusAccountValue() {
		Map<Integer, Integer> mCount = new HashMap<Integer, Integer>();
		StatusAccount[] arrEnum = StatusAccount.values();
		for (StatusAccount status : arrEnum) {
			mCount.put(status.order, 0);
		}
		return mCount;
	}
}
