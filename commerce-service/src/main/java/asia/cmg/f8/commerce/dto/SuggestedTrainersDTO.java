package asia.cmg.f8.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuggestedTrainersDTO {

	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("name")
	private String fullName;
	
	@JsonProperty("avatar")
	private String avatar;
	
	@JsonProperty("level")
	private String level;
	
	@JsonProperty("pt_booking_credit")
	private int ptBookingCredit;
	
	public SuggestedTrainersDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public SuggestedTrainersDTO(String uuid, String username, String fullName, String avatar, String level, int bookingCredit) {
		this.uuid = uuid;
		this.username = username;
		this.fullName = fullName;
		this.avatar = avatar;
		this.level = level;
		this.ptBookingCredit = bookingCredit;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getPtBookingCredit() {
		return ptBookingCredit;
	}

	public void setPtBookingCredit(int ptBookingCredit) {
		this.ptBookingCredit = ptBookingCredit;
	}
}
