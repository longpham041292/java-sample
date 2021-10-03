package asia.cmg.f8.profile.database.entity;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.profile.dto.DistrictDistanceDTO;
import asia.cmg.f8.profile.dto.LocationDistanceDTO;

@Entity
@Table(name = "district")
@SqlResultSetMapping(
		name = "districtDistanceDTOMapping",
		classes = {
			@ConstructorResult(
				targetClass = DistrictDistanceDTO.class,
		        columns = {
	        		@ColumnResult(name = "city_key"),
	        		@ColumnResult(name = "district_key"),
	        		@ColumnResult(name = "latitude"),
	        		@ColumnResult(name = "longtitude"),
	        		@ColumnResult(name = "distance_in_km")
		        }
			)
		}
	)
@NamedNativeQuery(name = "DistrictEntity.getTopNearestDistricts", 
				query = "SELECT city_key, `key` as district_key, latitude, longtitude, " + 
						"				((`DEGREES`(`ACOS`(LEAST(1.0, COS(RADIANS(?1)) " + 
						"		         * COS(`RADIANS`(latitude)) " + 
						"		         * COS(RADIANS(?2 - longtitude)) " + 
						"		         + SIN(RADIANS(?1)) " + 
						"		         * SIN(RADIANS(latitude)))))) * 111.111) AS distance_in_km " + 
						"FROM district " + 
						"WHERE `language` = 'en' " + 
						"ORDER BY distance_in_km " + 
						"LIMIT ?3",
				resultSetMapping = "districtDistanceDTOMapping")
public class DistrictEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "language")
	private String language;
	
	@Column(name = "`key`")
	private String key;
	
	@Column(name = "name")
	private String name;
	
	@JsonProperty("city_key")
	@Column(name = "city_key")
	private String cityKey;
	
	@Column(name = "`sequence`")
	private Integer sequence;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longtitude")
	private Double longtitude;
	
	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	@JsonIgnore
	private CityEntity city;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getCityKey() {
		return cityKey;
	}

	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public CityEntity getCity() {
		return city;
	}

	public void setCity(CityEntity city) {
		this.city = city;
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
