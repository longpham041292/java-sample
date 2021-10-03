package asia.cmg.f8.commerce.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentPartnerDTO {

	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("name")
	private String fullName;
	
	@JsonProperty("username")
	private String username;
	
	@JsonProperty("avatar")
	private String avartar;
	
	@JsonProperty("level")
	private String level;
	
	@JsonProperty("pt_booking_credit")
	private int ptBookingCredit;
	
	@JsonProperty("latest_booking_time")
	private Long latestBookingTime;

	public RecentPartnerDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public RecentPartnerDTO(String uuid, String username, String fullName, String avatar, String level, Integer bookingCredit, Date bookingTime) {
		this.uuid = uuid;
		this.username = username;
		this.fullName = fullName;
		this.avartar = avatar;
		this.level = level;
		this.ptBookingCredit = bookingCredit;
		this.latestBookingTime = bookingTime.getTime()/1000;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvartar() {
		return avartar;
	}

	public void setAvartar(String avartar) {
		this.avartar = avartar;
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

	public Long getBookingTime() {
		return latestBookingTime;
	}

	public void setBookingTime(Long bookingTime) {
		this.latestBookingTime = bookingTime;
	}
}
