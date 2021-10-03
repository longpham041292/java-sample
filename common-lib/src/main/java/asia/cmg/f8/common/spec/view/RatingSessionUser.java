package asia.cmg.f8.common.spec.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Created on 12/9/16.
 */
public interface RatingSessionUser {

    @JsonProperty("uuid")
    String getId();

    @JsonProperty("picture")
    String getPicture();

    @JsonProperty("full_name")
    String getFullName();

    @JsonProperty("stars")
    Double getStars();

    @JsonProperty("session_date")
    Long getSessionDate();

    @JsonProperty("reasons")
    List<String> getReasons();

    @JsonProperty("reaction")
    String getReaction();
    
    @JsonProperty("comment")
    @Nullable
    String getComment();
    
}
