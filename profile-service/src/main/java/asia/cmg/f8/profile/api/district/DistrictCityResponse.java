package asia.cmg.f8.profile.api.district;

import java.util.List;

import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;

public class DistrictCityResponse {
	private String language;
	private String city_key;
	private int sequence;
	private List<DistrictEntity> districts;
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCity_key() {
		return city_key;
	}
	public void setCity_key(String city_key) {
		this.city_key = city_key;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public List<DistrictEntity> getDistricts() {
		return districts;
	}
	public void setDistricts(List<DistrictEntity> districts) {
		this.districts = districts;
	}
}
