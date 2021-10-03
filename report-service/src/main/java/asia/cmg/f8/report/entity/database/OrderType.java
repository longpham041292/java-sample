package asia.cmg.f8.report.entity.database;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrderType {
	 @JsonProperty("product")
	 PRODUCT("PRODUCT"),
	 
	 @JsonProperty("subscription")
     SUBSCRIPTION("SUBSCRIPTION"),
	 
	 @JsonProperty("credit")
	 CREDIT("CREDIT"),
	 
	 @JsonProperty("coupon")
	 COUPON("COUPON");
	
	 private final String text;

    /**
     * @param text
     */
	OrderType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
   
}
