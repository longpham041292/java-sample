package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.spec.user.ExtendUserType;
import asia.cmg.f8.common.spec.user.GenderType;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/6/17.
 */
public class ProfileRequest {
    private String picture;
    private String name;
    private GenderType gender;
    private String country;
    private String city;
    private String birthday;
    private String phoneNumber;
    private String usercode;
    private String clubcode;
    private String email;
    private Boolean enableSubscribe;
    private Boolean enablePrivateChat;
    private ExtendUserType extUserType;
    
    @JsonProperty("extend_user_type")
    public ExtendUserType getExtUserType() {
		return extUserType;
	}

	public void setExtUserType(ExtendUserType extUserType) {
		this.extUserType = extUserType;
	}

	@JsonProperty("picture_url")
    public String getPicture() {
        return picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @JsonProperty("gender")
    public GenderType getGender() {
        return gender;
    }

    public void setGender(final GenderType gender) {
        this.gender = gender;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    @JsonProperty("birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(final String birthday) {
        this.birthday = birthday;
    }

    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @JsonProperty("usercode")
    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(final String usercode) {
        this.usercode = usercode;
    }
    
    @JsonProperty("clubcode")
    public String getClubcode() {
        return clubcode;
    }

    public void setClubcode(final String clubcode) {
        this.clubcode = clubcode;
    }
    
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @JsonProperty("enable_subscribe")
    public Boolean getEnableSubscribe() {
        return enableSubscribe;
    }

    public void setEnableSubscribe(final Boolean enableSubscribe) {
        this.enableSubscribe = enableSubscribe;
    }

    @JsonProperty("enable_private_chat")
	public Boolean getEnablePrivateChat() {
		return enablePrivateChat;
	}

	public void setEnablePrivateChat(Boolean enablePrivateChat) {
		this.enablePrivateChat = enablePrivateChat;
	}
}
