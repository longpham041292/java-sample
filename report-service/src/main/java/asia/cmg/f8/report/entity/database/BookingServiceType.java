package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingServiceType {
	SESSION,
	ETICKET,
	CLASS;
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
