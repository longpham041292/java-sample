package asia.cmg.f8.common.spec.session;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionTrainingStyle {
	OFFLINE, 
	ONLINE;
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
