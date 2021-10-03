package asia.cmg.f8.report.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PTClientsDTO {
	@JsonProperty("avatar")
	private String avatar;

	@JsonProperty("fullname")
	private String fullname;

	@JsonProperty("username")
	private String username;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("email")
	private String email;

	@JsonProperty("total_completed_session")
	private BigInteger totalCompletedSession;

	@JsonProperty("total_credit")
	private BigInteger totalCredit;

	public PTClientsDTO(String avatar, String fullname, String username, String phone, String email,
			BigInteger totalCompletedSession, BigInteger totalCredit) {
		super();
		this.avatar = avatar;
		this.fullname = fullname;
		this.username = username;
		this.phone = phone;
		this.email = email;
		this.totalCompletedSession = totalCompletedSession;
		this.totalCredit = totalCredit;
	}

	public PTClientsDTO() {
		super();
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigInteger getTotalCompletedSession() {
		return totalCompletedSession;
	}

	public void setTotalCompletedSession(BigInteger totalCompletedSession) {
		this.totalCompletedSession = totalCompletedSession;
	}

	public BigInteger getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(BigInteger totalCredit) {
		this.totalCredit = totalCredit;
	}

}
