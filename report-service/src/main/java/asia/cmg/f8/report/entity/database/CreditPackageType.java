package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CreditPackageType {
	UNIT, BASIC, GOLD, PLATINUM, DIAMOND;
	
	@JsonValue
    public int toValue() {
        return ordinal();
    }
}
