package asia.cmg.f8.common.spec.order;

import java.util.Date;

import javax.annotation.Nullable;

import asia.cmg.f8.common.spec.Identifiable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface OrderHistory extends Identifiable{

	@JsonProperty("code")
	@Nullable
	String getCode();
	
	@JsonProperty("trainer_name")
	@Nullable
	String getTrainerName();
	
	@JsonProperty("created_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
	@Nullable
	Date getCreatedDate();
    
    @JsonProperty("display_total_price")
    @Nullable
    String getDisplayTotalPrice();

    @JsonProperty("num_of_session")
    @Nullable
    Integer getNumberOfSession();

    @JsonProperty("expire_in")
    @Nullable
    Integer getExpireIn();
}
