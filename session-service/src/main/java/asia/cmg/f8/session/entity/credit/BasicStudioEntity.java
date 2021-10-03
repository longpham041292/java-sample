package asia.cmg.f8.session.entity.credit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = "studios")
public class BasicStudioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "uuid", length = 50)
	private String uuid;
	
	@Column(name = "name", length = 255)
	private String name;
	
	@Column(name = "address", length = 1000)
	private String address;
	
	@Column(name = "longitude")
	private Double longitude;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "image", length = 255)
	private String image;
	
	@Column(name = "logo", length = 255)
	private String logo;
	
	@Column(name = "phone", length = 255)
	private String phone;
	
	@Column(name = "checkin_credit")
	@JsonProperty("checkin_credit")
	private Integer checkinCredit;
	
	@Column(name = "active")
	private Boolean active;
	
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
}
