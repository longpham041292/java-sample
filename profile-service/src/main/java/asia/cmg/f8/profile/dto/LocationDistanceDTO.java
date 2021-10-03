package asia.cmg.f8.profile.dto;


import com.fasterxml.jackson.annotation.JsonProperty;


public class LocationDistanceDTO implements Comparable<LocationDistanceDTO>{

	@JsonProperty("district_key")
	private String districtKey;
	
//	@JsonProperty("district_name")
//	private String districtName;
	
//	@JsonProperty("city_key")
//	private String cityKey;
//	
//	@JsonProperty("city_name")
//	private String cityName;
	
	@JsonProperty("latitude")
	private Double latitude;
	
	@JsonProperty("longtitude")
	private Double longtitude;
	
	@JsonProperty("user_uuid")
	private String userUuid;
	
	@JsonProperty("distance_in_km")
	private Double distanceInKm;
	
	public LocationDistanceDTO() {
	}
	
	public LocationDistanceDTO(final String districtKey,
								final Double latitude,
								final Double longtitude,
								final String userUuid,
								final Double distanceInKm) {
		
		this.districtKey = districtKey;
//		this.cityKey = cityKey;
//		this.cityName = cityName;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.userUuid = userUuid;
		this.distanceInKm = distanceInKm;
	}

	public String getDistrictKey() {
		return districtKey;
	}

	public void setDistrictKey(String districtKey) {
		this.districtKey = districtKey;
	}

//	public String getDistrictName() {
//		return districtName;
//	}
//
//	public void setDistrictName(String districtName) {
//		this.districtName = districtName;
//	}

//	public String getCityKey() {
//		return cityKey;
//	}
//
//	public void setCityKey(String cityKey) {
//		this.cityKey = cityKey;
//	}
//
//	public String getCityName() {
//		return cityName;
//	}
//
//	public void setCityName(String cityName) {
//		this.cityName = cityName;
//	}

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

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public Double getDistanceInKm() {
		return distanceInKm;
	}

	public void setDistanceInKm(Double distanceInKm) {
		this.distanceInKm = distanceInKm;
	}

	@Override
	public int compareTo(LocationDistanceDTO object) {
		return this.distanceInKm.compareTo(object.getDistanceInKm());
	}
}
