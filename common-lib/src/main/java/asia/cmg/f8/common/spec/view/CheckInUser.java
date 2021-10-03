package asia.cmg.f8.common.spec.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created on 12/7/16.
 */
public interface CheckInUser {

    @JsonProperty("uuid")
    String getId();
    
    @JsonProperty("name")
    String getName();

    @JsonProperty("username")
    String getUsername();

    @JsonProperty("picture")
    @Nullable
    String getPicture();

    @JsonProperty("followed")
    Boolean isFollowed();
}
