package asia.cmg.f8.profile.database.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "city")
public class CityEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "`key`")
	private String key;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "language")
	private String language;
	
	@Column(name = "`sequence`")
	private Integer sequence;
	
	@Column(name = "latitude")
	private Double latitude;
	
	@Column(name = "longtitude")
	private Double longtitude;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "city", targetEntity = DistrictEntity.class)
	private List<DistrictEntity> districts = new ArrayList<DistrictEntity>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public List<DistrictEntity> getDistricts() {
		return districts;
	}
	
	public Double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}

	public void setDistricts(List<DistrictEntity> districts) {
		this.districts = districts;
		districts.forEach(district -> {
			district.setCity(this);
		});
	}
	
	public void setDistrict(DistrictEntity district) {
		this.districts.add(district);
		district.setCity(this);
	}
}
