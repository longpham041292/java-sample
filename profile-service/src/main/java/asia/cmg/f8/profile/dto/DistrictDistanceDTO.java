package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistrictDistanceDTO {

	@JsonProperty("city_key")
	private String cityKey;
	
	@JsonProperty("district_key")
	private String districtKey;
	
	@JsonProperty("latitude")
	private Double latitude;
	
	@JsonProperty("longtitude")
	private Double longtitude;
	
	@JsonProperty("distance_in_km")
	private Double distanceInKm;
	
	public DistrictDistanceDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public DistrictDistanceDTO(String cityKey, String districtKey, Double lat, Double longt, Double distance) {
		this.cityKey = cityKey;
		this.districtKey = districtKey;
		this.latitude = lat;
		this.longtitude = longt;
		this.distanceInKm = distance;
	}

	public String getCityKey() {
		return cityKey;
	}

	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	public String getDistrictKey() {
		return districtKey;
	}

	public void setDistrictKey(String districtKey) {
		this.districtKey = districtKey;
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

	public Double getDistanceInKm() {
		return distanceInKm;
	}

	public void setDistanceInKm(Double distanceInKm) {
		this.distanceInKm = distanceInKm;
	}
}
