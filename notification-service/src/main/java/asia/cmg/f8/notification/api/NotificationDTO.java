package asia.cmg.f8.notification.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationDTO {

	@JsonProperty("receiver")
	public String receiver;
	
	@JsonProperty("aps_payload")
	public String apsPayload;
	
	@JsonProperty("firebase_payload")
	public String firebasePayload;
}
