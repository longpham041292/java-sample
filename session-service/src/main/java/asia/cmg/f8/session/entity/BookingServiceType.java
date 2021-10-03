package asia.cmg.f8.session.entity;

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
