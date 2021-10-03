package asia.cmg.f8.profile.database.entity;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "trending_user_district", uniqueConstraints = @UniqueConstraint(columnNames = {"district_key", "user_uuid"}))
public class TrendingUserDistrictEntity implements Serializable {

	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "city_key")
	@JsonProperty("city_key")
	private String cityKey;
	
	@Column(name = "district_key")
	@JsonProperty("district_key")
	private String districtKey;
	
	@Column(name = "`order`", columnDefinition = "int default 0")
	@JsonProperty("order")
	private Integer order;
	
	@Column(name = "user_uuid")
	@JsonProperty("user_uuid")
	private String userUuid;
	
	@Column(name = "user_type")
	@JsonProperty("user_type")
	private String userType;
	
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	@JsonIgnore
	private Date createdDate;
	
	@UpdateTimestamp
	@Column(name = "modified_date")
	@JsonIgnore
	private Date modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}
