package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.BasicUserEntity;

/**
 * Created on 11/28/16.
 */
public class BasicUserInfo {

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("name")
	private String name;

	@JsonProperty("username")
	private String username;

	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("email")
	private String email;

	@JsonProperty("city")
	private String city;

	@JsonProperty("country")
	private String country;

	@JsonProperty("activated")
	private Boolean activated;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("language")
	private String language;

	@JsonProperty("verify_phone")
	private Boolean verifyPhone = false;

	public static BasicUserInfo convertFromEntity(final BasicUserEntity userEntity) {
		final BasicUserInfo userInfo = new BasicUserInfo();

		userInfo.setUuid(userEntity.getUuid());
		userInfo.setName(userEntity.getFullName());
		userInfo.setAvatar(userEntity.getAvatar());
		userInfo.setEmail(userEntity.getEmail());
		userInfo.setCity(userEntity.getCity());
		userInfo.setCountry(userEntity.getCountry());
		userInfo.setActivated(userEntity.isActivated());
		userInfo.setUsername(userEntity.getUserName());
		userInfo.setPhone(userEntity.getPhone());
		userInfo.setVerifyPhone(userEntity.getVerifyPhone());

		return userInfo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(final String avatar) {
		this.avatar = avatar;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(final Boolean activated) {
		this.activated = activated;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getVerifyPhone() {
		return verifyPhone;
	}

	public void setVerifyPhone(Boolean verifyPhone) {
		this.verifyPhone = verifyPhone;
	}

}
