package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CMCSendSMSResponse {
	@JsonProperty(value = "Code")
	private Integer code;
	@JsonProperty(value = "Description")
	private String description;
	@JsonProperty(value = "Data")
	private CMCDataResponse data;
	
	
	public CMCSendSMSResponse() {
		super();
	}


	public CMCSendSMSResponse(Integer code, String description, CMCDataResponse data) {
		super();
		this.code = code;
		this.description = description;
		this.data = data;
	}


	public Integer getCode() {
		return code;
	}


	public void setCode(Integer code) {
		this.code = code;
	}


	public String getDescription() {
		return this.description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public CMCDataResponse getData() {
		return this.data;
	}


	public void setData(CMCDataResponse data) {
		this.data = data;
	}
}
