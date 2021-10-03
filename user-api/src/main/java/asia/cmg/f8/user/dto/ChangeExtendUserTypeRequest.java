package asia.cmg.f8.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.common.spec.user.ExtendUserType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeExtendUserTypeRequest {

	@JsonProperty(value = "uuid")
	private String userUuid;
	
	@JsonProperty(value = "extend_user_type")
	private ExtendUserType extUserType;

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public ExtendUserType getExtUserType() {
		return extUserType;
	}

	public void setExtUserType(ExtendUserType extUserType) {
		this.extUserType = extUserType;
	}
}
