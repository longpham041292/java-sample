package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserGrantType {
	@JsonProperty("password")
	PASSWORD,
	@JsonProperty("facebook")
	FACEBOOK,
	@JsonProperty("apple")
	APPLE
}
