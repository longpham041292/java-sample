package asia.cmg.f8.gateway.security.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.gateway.security.dto.UserInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;

/**
 * Created on 11/3/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface UserDetail extends Serializable {

    @JsonProperty("on_board_completed")
    boolean isOnBoarded();
    
    @JsonProperty("onBoardCompleted")
    boolean isOnBoardCompleted();

    @JsonProperty("userType")
    String getUserType();

	@JsonProperty("permissions")
	String getPermissions();

    @JsonProperty("uuid")
    String getUuid();

    @JsonProperty("activated")
    boolean isActivated();

    @JsonProperty("username")
    String getUserName();

    @JsonProperty("picture")
    String getPicture();

    @JsonProperty("name")
    String getName();

    @JsonProperty("language")
    String getLanguage();

    @JsonProperty("document_approval_status")
    @Nullable
    String getDocumentApprovalStatus();

    @JsonProperty("fb_id")
    @Nullable
    String getFacebookId();
    
    @JsonProperty("apple_id")
    @Nullable
    String getAppleId();
    
    @JsonProperty("extend_user_type")
    @Nullable
    ExtendUserType getExtendUserType();
    
    @JsonProperty("last_question_sequence")
    @Nullable
    Integer getLastQuestionSequence();
    
    @JsonProperty("updated_profile")
    Boolean updatedProfile();
}
