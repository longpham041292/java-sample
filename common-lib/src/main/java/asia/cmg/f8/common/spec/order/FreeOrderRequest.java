package asia.cmg.f8.common.spec.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface FreeOrderRequest {
    
    @JsonProperty("pt_uuid")
    String getPtUuid();
    
    @JsonProperty("eu_uuid")
    String getEuUuid();
    
    @JsonProperty("num_of_sessions")
    int getNumOfSessions();
    
    @JsonProperty("pt_service_fee")
    double getPtServiceFree();
    
    @JsonProperty("expire_date")
    String getExpireDate();
}
