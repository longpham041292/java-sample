package asia.cmg.f8.user.domain.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminChangeNameRequest {
	private String name;
    private String uuid;
    
    @JsonProperty("name")
	public String getName() {
		return name;
	}
    
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
    
    
}
