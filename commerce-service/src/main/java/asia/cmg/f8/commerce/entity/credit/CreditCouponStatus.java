package asia.cmg.f8.commerce.entity.credit;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditCouponStatus {
	ACTIVED, USED, EXPIRED;

	@JsonValue
	public int toValue() {
		return ordinal();
	}
}
