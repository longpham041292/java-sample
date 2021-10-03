package asia.cmg.f8.common.spec.view;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 12/15/16.
 */
public interface CompletedSessionView {
    @JsonProperty("uuid")
    String getSessionId();

    @JsonProperty("partner_id")
    String getPartnerId();

    @JsonProperty("partner_full_name")
    String getPartnerFullName();

    @JsonProperty("partner_picture")
    String getPartnerPicture();
    
    @JsonProperty("session_date")
    Long getSessionDate();
    
    @JsonProperty("partner_username")
    String getPartnerUsername();
}
