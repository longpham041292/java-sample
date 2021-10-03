package asia.cmg.f8.notification.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EPhoneType {
	@JsonProperty("ios")
	IOS,
	@JsonProperty("android")
	ANDROID;
}
