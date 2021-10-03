package asia.cmg.f8.commerce.entity.credit;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "studios", uniqueConstraints = @UniqueConstraint(name = "uuid_UN", columnNames = {"uuid"}))
public class StudioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "uuid", length = 50)
	private String uuid;
	
	@Column(name = "name", length = 255)
	private String name;
	
	@Column(name = "address", length = 1000)
	private String address;
	
	@Column(name = "longitude", columnDefinition = "double not null default 0")
	private Double longitude;
	
	@Column(name = "latitude", columnDefinition = "double not null default 0")
	private Double latitude;
	
	@Column(name = "image", length = 255)
	private String image;
	
	@Column(name = "logo", length = 255)
	private String logo;
	
	@Column(name = "facebook", length = 255)
	private String facebook;
	
	@Column(name = "website", length = 255)
	private String website;
	
	@Column(name = "phone", length = 255)
	private String phone;
	
	@Column(name = "checkin_credit", columnDefinition = "int not null default 0")
	@JsonProperty("checkin_credit")
	private Integer checkinCredit;
	
	@Column(name = "open_at")
	@JsonProperty("open_at")
	private LocalTime openAt;
	
	@JsonProperty("close_at")
	@Column(name = "close_at")
	private LocalTime closeAt;
	
	@Column(name = "active", columnDefinition = "tinyint not null default 1")
	private Boolean active;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonIgnore
	private LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private LocalDateTime modifiedDate;
	
	@Column(name = "categories", length = 255)
	private String categories;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(LocalDateTime modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}
}
