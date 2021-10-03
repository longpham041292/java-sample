package asia.cmg.f8.commerce.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CmsCreateWalletRequest {

	@JsonProperty("studio_uuid")
	@NotNull
	private String studioUuid;
	
	@JsonProperty("studio_name")
	@NotNull
	private String studioName;
	
	public String getStudioUuid() {
		return studioUuid;
	}

	public void setStudioUuid(String studioUuid) {
		this.studioUuid = studioUuid;
	}

	public String getStudioName() {
		return studioName;
	}

	public void setStudioName(String studioName) {
		this.studioName = studioName;
	}
}
