package asia.cmg.f8.profile.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictCollection {

	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("language")
	private String language;
	
	@JsonProperty("key")
	private String key;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("city_key")
	private String city_key;
	
	@JsonProperty("sequence")
	private String sequence;
	
	@JsonProperty("latitude")
	private Double latitude;
	
	@JsonProperty("longtitude")
	private Double longtitude;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity_key() {
		return city_key;
	}

	public void setCity_key(String city_key) {
		this.city_key = city_key;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}
}
