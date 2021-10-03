package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchedTrainersDTO {

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
	
	@JsonProperty("booking_credit")
	private Integer bookingCredit = 0;
	
	@JsonProperty("user_ptimatch")
	private PTIMatchDTO userPTiMatch;

	public MatchedTrainersDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public MatchedTrainersDTO(String ptUuid, String username, String fullName, String avatar,
							Double average, Double personality, Double trainingStyle, Double interest,  
							String ptLevel, Integer ptBookingCredit) {
		this.uuid = ptUuid;
		this.username = username;
		this.fullName = fullName;
		this.avatar = avatar;
		this.level = ptLevel;
		this.bookingCredit = ptBookingCredit;
		PTIMatchDTO ptiMatch = new PTIMatchDTO(personality, trainingStyle, interest, average);
		this.userPTiMatch = ptiMatch;
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

	public Integer getBookingCredit() {
		return bookingCredit;
	}

	public void setBookingCredit(Integer bookingCredit) {
		this.bookingCredit = bookingCredit;
	}

	public PTIMatchDTO getUserPTiMatch() {
		return userPTiMatch;
	}

	public void setUserPTiMatch(PTIMatchDTO userPTiMatch) {
		this.userPTiMatch = userPTiMatch;
	}
}
