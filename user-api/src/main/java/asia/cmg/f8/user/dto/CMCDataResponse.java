package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CMCDataResponse {
	@JsonProperty(value = "Brandname")
	private String brandname;
	@JsonProperty(value = "Phonenumber")
	private String phonenumber;
	@JsonProperty(value = "Message")
	private String message;
	@JsonProperty(value = "Status")
	private Integer status;
	@JsonProperty(value = "StatusDescription")
	private String statusDescription;
	
	public CMCDataResponse(String brandname, String phonenumber, String message, Integer status,
			String statusDescription) {
		super();
		this.brandname = brandname;
		this.phonenumber = phonenumber;
		this.message = message;
		this.status = status;
		this.statusDescription = statusDescription;
	}
	public CMCDataResponse() {
		super();
	}
	
	public String getBrandname() {
		return this.brandname;
	}
	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}
	public String getPhonenumber() {
		return this.phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getStatus() {
		return this.status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusDescription() {
		return this.statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}		
}