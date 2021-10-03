package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppleUserPayloadDTO {
	@JsonProperty(value = "iss")
	private String iss;
	
	@JsonProperty(value = "aud")
	private String aud;
	
	@JsonProperty(value = "exp")
	private String exp;
	
	@JsonProperty(value = "iat")
	private String iat;
	
	@JsonProperty(value = "sub")
	private String sub;
	
	@JsonProperty(value = "at_hash")
	private String at_hash;
	
	@JsonProperty(value = "email")
	private String email;
	
	@JsonProperty(value = "email_verified")
	private String email_verified;
	
	@JsonProperty(value = "is_private_email")
	private String is_private_email;
	
	public AppleUserPayloadDTO() {
		super();
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getIat() {
		return iat;
	}

	public void setIat(String iat) {
		this.iat = iat;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getAt_hash() {
		return at_hash;
	}

	public void setAt_hash(String at_hash) {
		this.at_hash = at_hash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail_verified() {
		return email_verified;
	}

	public void setEmail_verified(String email_verified) {
		this.email_verified = email_verified;
	}

	public String getIs_private_email() {
		return is_private_email;
	}

	public void setIs_private_email(String is_private_email) {
		this.is_private_email = is_private_email;
	}
	
	
}
