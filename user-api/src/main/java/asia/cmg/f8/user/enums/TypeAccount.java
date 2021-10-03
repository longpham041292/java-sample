package asia.cmg.f8.user.enums;

import java.util.HashMap;
import java.util.Map;

public enum TypeAccount {
	/** End User */
	CLIENT(0),
	/** Personal Trainer */
	TRAINNER(1),
	/** Club */
	CLUB(2),
	/** Admin */
	ADMIN(3);

	private int order;

	TypeAccount(int order) {
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
			return "CLIENT";
		case 1:
			return "TRAINNER";
		case 2:
			return "CLUB";
		case 3:
			return "ADMIN";
		default:
			return "";
		}
	}

	public static TypeAccount getValueOf(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Map<String, Integer> buildMapTypeAccountName() {
		Map<String, Integer> mCount = new HashMap<String, Integer>();
		TypeAccount[] arrEnum = TypeAccount.values();
		for (TypeAccount type : arrEnum) {
			mCount.put(type.name(), 0);
		}
		return mCount;
	}
	
	public static Map<Integer, Integer> buildMapTypeAccountValue() {
		Map<Integer, Integer> mCount = new HashMap<Integer, Integer>();
		TypeAccount[] arrEnum = TypeAccount.values();
		for (TypeAccount type : arrEnum) {
			mCount.put(type.order, 0);
		}
		return mCount;
	}
}
