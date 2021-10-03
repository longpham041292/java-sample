package asia.cmg.f8.commerce.entity.credit;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WithdrawalStatus {
	PENDING("PENDING", 0),
	WITHDRAWED("WITHDRWED", 1);
	
	private String text;
	private int value;
	
	private WithdrawalStatus(String text, int value) {
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