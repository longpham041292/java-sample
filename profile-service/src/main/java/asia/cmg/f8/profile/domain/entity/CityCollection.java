package asia.cmg.f8.profile.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityCollection {
	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("key")
	private String key;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("language")
	private String language;
	
	@JsonProperty("sequence")
	private Integer sequence;
	
	@JsonProperty("latitude")
	private Double latitude;
	
	@JsonProperty("longtitute")
	private Double longtitute;
	
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongtitute() {
		return longtitute;
	}

	public void setLongtitute(Double longtitute) {
		this.longtitute = longtitute;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getUuid() {
		return uuid;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public Integer getSequence() {
		return sequence;
	}
}
