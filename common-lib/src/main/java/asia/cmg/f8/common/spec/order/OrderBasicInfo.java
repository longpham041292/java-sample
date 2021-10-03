package asia.cmg.f8.common.spec.order;

import asia.cmg.f8.common.spec.Identifiable;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface OrderBasicInfo extends Identifiable{

	@JsonProperty("order_code")
	String getOrderCode();
	
	@JsonProperty("created_date")
	long getCreatedDate();
	
	@JsonProperty("num_of_sessions")
	int getNumberOfSession();
	
	@JsonProperty("expire_limit")
	int getExpireLimit();
	
	@JsonProperty("package_price")
	double getPackagePrice();
	
	@JsonProperty("commision")
	double getCommision();
	
	@JsonProperty("currency")
	String getCurrency();
	
	@JsonProperty("user_Uuid")
	String getUserUuid();
	
	@JsonProperty("pt_uuid")
	String getPtUuid();
}
