package asia.cmg.f8.profile.database.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.profile.dto.LocationDistanceDTO;

@Table(name = "user_district_location", uniqueConstraints = @UniqueConstraint(name = "user_district_UNQ", columnNames = {"district_key", "user_uuid"}))
@Entity
@SqlResultSetMapping(
		name = "locationDistanceDTOMapping",
		classes = {
			@ConstructorResult(
				targetClass = LocationDistanceDTO.class,
		        columns = {
	        		@ColumnResult(name = "district_key"),
	        		@ColumnResult(name = "latitude"),
	        		@ColumnResult(name = "longtitude"),
	        		@ColumnResult(name = "user_uuid"),
	        		@ColumnResult(name = "distance_in_km")
		        }
			)
		}
	)
@NamedNativeQuery(name = "UserDistrictLocationEntity.getTopNearestDistrictLocation", 
query = "SELECT t.district_key, t.latitude, t.longtitude, t.user_uuid, t.distance_in_km " + 
		"FROM " + 
		"	(SELECT DISTINCT latitude, longtitude, district_key, user_uuid, " + 
		"				((DEGREES(ACOS(LEAST(1.0, COS(RADIANS(?1)) " + 
		"		         * COS(RADIANS(latitude)) " + 
		"		         * COS(RADIANS(?2 - longtitude)) " + 
		"		         + SIN(RADIANS(?1)) " + 
		"		         * SIN(RADIANS(latitude)))))) * 111.111) distance_in_km " + 
		"	FROM user_district_location udl JOIN session_users pt ON udl.user_uuid = pt.uuid AND udl.user_type = 'pt' AND pt.doc_status = 'approved' " +
		"	) AS t " + 
		"WHERE t.distance_in_km <= ?3 " +
		"GROUP BY user_uuid " +
		"ORDER BY t.distance_in_km " + 
		"LIMIT ?4", 
resultSetMapping = "locationDistanceDTOMapping")
public class UserDistrictLocationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_uuid", length = 50)
	@JsonProperty("user_uuid")
	private String userUuid;
	
	@Column(name = "user_type", length = 20)
	@JsonProperty("user_type")
	private String userType;
	
	@Column(name = "district_key", length = 100)
	@JsonProperty("district_key")
	private String districtKey;
	
	@Column(name = "city_key", length = 100)
	@JsonProperty("city_key")
	private String cityKey;
	
	@Column(name = "latitude")
	private double latitude = 0;
	
	@Column(name = "longtitude")
	private double longtitude = 0;
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

	@JsonIgnore
	@UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getDistrictKey() {
		return districtKey;
	}

	public void setDistrictKey(String districtKey) {
		this.districtKey = districtKey;
	}

	public String getCityKey() {
		return cityKey;
	}

	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
}
