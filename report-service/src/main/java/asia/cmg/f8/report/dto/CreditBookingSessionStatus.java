package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created on 11/22/16.
 */
public enum CreditBookingSessionStatus {

	BOOKED,

	REJECTED,

	CONFIRMED,

	// Per requirement, we should remove when return to client
	CANCELLED,

	// Per requirement, we should remove when return to client
	TRAINER_CANCELLED,

	// Per requirement, we should change it to Cancelled when return to client
	EU_CANCELLED,

	// Per requirement, we should change it to Burned when return to client
	BURNED,

	COMPLETED,

	DEDUCTED,

	REFUNDED;

	@JsonValue
	public int toValue() {
		return ordinal();
	}
}
