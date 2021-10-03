package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditTransactionStatus {
	FAILED("FAILED", 0), 
	COMPLETED("COMPLETED", 1);
	
	private String text;
	private int value;
	
	private CreditTransactionStatus(String text, int value) {
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
