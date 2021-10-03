package asia.cmg.f8.user.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsergridChangeNameApi {
	private String name;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	
	
}
