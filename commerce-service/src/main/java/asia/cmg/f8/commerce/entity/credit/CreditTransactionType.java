package asia.cmg.f8.commerce.entity.credit;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditTransactionType {
	UNDEFINED("UNDEFINED",0),
	TOPUP("TOPUP", 1),
	STUDIO_CHECKIN("STUDIO_CHECKIN", 2),
	EXPIRED_CREDITS("EXPIRED_CREDITS", 3),
	BOOKING_SESSION("BOOKING_SESSION", 4),
	PAY_COMPLETED_SESSION("PAY_COMPLETED_SESSION", 5),
	PAY_BURNED_SESSION("PAY_BURNED_SESSION", 6),
	REFUND_CANCEL_SESSION("REFUND_CANCEL_SESSION", 7),
	WITHDRAW_CREDITS("WITHDRAW_CREDITS", 8),
	WALLET_LEVEL_UPGRADE("WALLET_LEVEL_UPGRADE", 9),
	BOOKING_CLASS("BOOKING_CLASS", 10),
	BOOKING_ETICKET("BOOKING_ETICKET", 11),
	REFUND_CANCEL_ETICKET("REFUND_CANCEL_ETICKET", 12),
	REFUND_CANCEL_CLASS("REFUND_CANCEL_CLASS", 13),
	PAY_BURNED_CLASS("PAY_BURNED_CLASS",14),
	PAY_BURNED_ETICKET("PAY_BURNED_ETICKET",15),
	PAY_COMPLETED_CLASS("PAY_COMPLETED_CLASS", 16),
	PAY_COMPLETED_ETICKET("PAY_COMPLETED_ETICKET",17),
	UPGRADE_ETICKET("UPGRADE_ETICKET",18);
	
	private String text;
	private int value;
	
	private CreditTransactionType(String text, int value) {
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public int getValue() {
		return value;
	}
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
