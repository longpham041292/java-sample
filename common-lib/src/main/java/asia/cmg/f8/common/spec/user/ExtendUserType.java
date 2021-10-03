package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ExtendUserType {
	@JsonProperty("pt_normal")
	PT_NORMAL,
	@JsonProperty("pt_influencer")
	PT_INFLUENCER,
	@JsonProperty("pt_ambassador")
	PT_AMBASSADOR,
	@JsonProperty("pt_leader")
	PT_LEADER;
}
