package asia.cmg.f8.user.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum RoleEnum {
	/** End User */
	CLIENT(0),
	/** Personal Trainer */
	TRAINNER(1),
	/** Club */
	CLUB(2),
	/** Admin */
	ADMIN(3);

	private int order;

	RoleEnum(int order) {
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
	public static List<String> getRole(int order) {
		switch (order) {
		case 0:
			return Arrays.asList(RoleEnum.CLIENT.name());
		case 1:
			return Arrays.asList(RoleEnum.TRAINNER.name());
		case 2:
			return Arrays.asList(RoleEnum.CLUB.name());
		case 3:
			return Arrays.asList(RoleEnum.ADMIN.name());
		default:
			return new ArrayList<String>();
		}
	}
	
	public static List<RoleEnum> getClientTrainner() {
        return Arrays.asList(RoleEnum.CLIENT, RoleEnum.TRAINNER);
    }
	public static RoleEnum getValueOf(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static Map<String, Integer> buildMapTypeAccountName() {
		Map<String, Integer> mCount = new HashMap<String, Integer>();
		RoleEnum[] arrEnum = RoleEnum.values();
		for (RoleEnum type : arrEnum) {
			mCount.put(type.name(), 0);
		}
		return mCount;
	}
	
	public static Map<Integer, Integer> buildMapTypeAccountValue() {
		Map<Integer, Integer> mCount = new HashMap<Integer, Integer>();
		RoleEnum[] arrEnum = RoleEnum.values();
		for (RoleEnum type : arrEnum) {
			mCount.put(type.order, 0);
		}
		return mCount;
	}
}
