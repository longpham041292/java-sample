package asia.cmg.f8.common.spec.user;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an user's entity
 * Created on 10/11/16.
 */
@SuppressWarnings("PMD")
public interface IUserEntity {

    @JsonProperty("uuid")
    @Nullable
    String getUuid();

    @JsonProperty("name")
    @Nullable
    String getName();

    @JsonProperty("password")
    @Nullable
    String getPassword();

    @JsonProperty("email")
    @Nullable
    String getEmail();

    @JsonProperty("username")
    @Nullable
    String getUsername();
    
    @JsonProperty("usercode")
    @Nullable
    String getUsercode();
    
    @JsonProperty("clubcode")
    @Nullable
    String getClubcode();

    @JsonProperty("picture")
    @Nullable
    String getPicture();

    @JsonProperty("language")
    @Nullable
    String getLanguage();

    @JsonProperty("userType")
    @Nullable
    UserType getUserType();

    @JsonProperty("country")
    @Nullable
    String getCountry();

    @JsonProperty("created")
    @Nullable
    Long getCreated();

    @JsonProperty("skills")
    @Nullable
    String getSkills();
    
    @JsonProperty("approved")
    @Nullable
    Boolean getApproved();

    @JsonProperty("activated")
    @Nullable
    Boolean getActivated();
    
    @JsonProperty("emailvalidated")
    @Nullable
    Boolean getEmailvalidated();
    
    @JsonProperty("phone_validated")
    @Nullable
    Boolean getPhoneValidated();

    @JsonProperty("profile")
    @Nullable
    IProfile getProfile();

    @JsonProperty("status")
    @Nullable
    IUserStatus getStatus();

    @JsonProperty("facebook")
    @Nullable
    IFacebook getFacebook();
    
    @JsonProperty("auth_provider_id")
    @Nullable
    String getAuthProviderId();
    
    @JsonProperty("updated_profile")
    @Nullable
    Boolean getUpdatedProfile();
    
    @JsonProperty("grant_type")
    @Nullable
    UserGrantType getGrantType();

    /**
     * Because there is an issue with user-grid when it try to query user "facebook.id=something". It sometime returns data, sometime it's not.
     * The workaround is to move user's facebook id property to the same level of root user object to avoid querying issue.
     *
     * @return the facebook id of user. Method may return null value.
     */
    @JsonProperty("fbId")
    @Nullable
    String getFacebookId();
    
    @JsonProperty("appleId")
    @Nullable
    String getAppleId();

    @JsonProperty("level")
    @Nullable
    String getLevel();
    
    @JsonProperty("lastMsg")
    @Nullable
    String getLastMsg();
    
    @JsonProperty("onBoardCompleted")
    @Nullable
    Boolean getOnBoardCompleted();

    @JsonProperty("enable_subscribe")
    @Nullable
    Boolean getEnableSubscribe();
    
    @JsonProperty("extend_user_type")
    @Nullable
    ExtendUserType getExtUserType();
    
    @JsonProperty("enable_private_chat")
    @Nullable
    Boolean getEnablePrivateChat();
}