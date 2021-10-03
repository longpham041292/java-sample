package asia.cmg.f8.profile.domain.entity.home;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EContentType {
	@JsonProperty("html")
	HTML("html"),
	@JsonProperty("text")
	TEXT("text");
	
	private String text;
	
	private EContentType(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
