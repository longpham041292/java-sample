package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CmcSmsRequest {
	@JsonProperty(value = "Brandname")
	private String brandname;
	
	@JsonProperty(value = "Message")
	private String message;
	
	@JsonProperty(value = "Phonenumber")
	private String phonenumber;
	
	@JsonProperty(value = "user")
	private String user;
	
	@JsonProperty(value = "pass")
	private String pass;

	
	
	public CmcSmsRequest() {
		super();
	}

	public CmcSmsRequest(String brandname, String message, String phonenumber, String user, String pass) {
		super();
		this.brandname = brandname;
		this.message = message;
		this.phonenumber = phonenumber;
		this.user = user;
		this.pass = pass;
	}

	public String getBrandname() {
		return this.brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPhonenumber() {
		return this.phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}	
}
