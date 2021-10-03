package asia.cmg.f8.commerce.entity.credit;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Partner {
	LEEP, CMS;
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
