package asia.cmg.f8.commerce.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudioDto {
	
	/**
	 * @param id
	 * @param uuid
	 * @param name
	 * @param address
	 * @param longitude
	 * @param latitude
	 * @param image
	 * @param logo
	 * @param facebook
	 * @param website
	 * @param phone
	 * @param checkinCredit
	 * @param openAt
	 * @param closeAt
	 * @param active
	 */
	public StudioDto(Integer id, String uuid, String name, String address, Double longitude, Double latitude,
			String image, String logo, String facebook, String website, String phone, Integer checkinCredit,
			LocalTime openAt, LocalTime closeAt) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.image = image;
		this.logo = logo;
		this.facebook = facebook;
		this.website = website;
		this.phone = phone;
		this.checkinCredit = checkinCredit;
		this.openAt = openAt;
		this.closeAt = closeAt;
	}

	public StudioDto() {
		// TODO Auto-generated constructor stub
	}
	
	private Integer id;
	
	private String uuid;
	
	private String name;
	
	private String address;
	
	private Double longitude;
	
	private Double latitude;
	
	private String image;
	
	private String logo;
	
	private String facebook;
	
	private String website;
	
	private String phone;
	
	@JsonProperty("checkin_credit")
	private Integer checkinCredit;
	
	private LocalTime openAt;
	
	private LocalTime closeAt;
	
	private String workingTime;
	
	public String getWorkingTime() {
		if(workingTime == null || workingTime.isEmpty() && openAt != null && closeAt != null)
			workingTime = openAt.toString().concat(" - ").concat(closeAt.toString());
		return workingTime;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getCheckinCredit() {
		return checkinCredit;
	}

	public void setCheckinCredit(Integer checkinCredit) {
		this.checkinCredit = checkinCredit;
	}

	public LocalTime getOpenAt() {
		return openAt;
	}

	public void setOpenAt(LocalTime openAt) {
		this.openAt = openAt;
	}

	public LocalTime getCloseAt() {
		return closeAt;
	}

	public void setCloseAt(LocalTime closeAt) {
		this.closeAt = closeAt;
	}

}
