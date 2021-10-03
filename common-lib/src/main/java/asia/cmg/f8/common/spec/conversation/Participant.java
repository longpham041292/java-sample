package asia.cmg.f8.common.spec.conversation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/15/16.
 */
public interface Participant {

    @JsonProperty("uuid")
    String getUuid();

    @JsonProperty("name")
    String getName();

    @JsonProperty("picture")
    String getPicture();
    
    @JsonProperty("lastMsg")
    String getLastMsg();
    
}
