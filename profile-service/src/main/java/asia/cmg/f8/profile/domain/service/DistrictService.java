package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.profile.api.district.DistrictCityResponse;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.domain.client.DistrictClient;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;

@Component
public class DistrictService {

	private Logger LOG = LoggerFactory.getLogger(DistrictService.class);
	private final int LIMIT = 100;
	
	@Autowired
	private DistrictClient districtClient;
	
	@Autowired DistrictRepository districtRepo;
	
	public List<DistrictCollection> searchDistrictsByName(final String token, final String keyWord, final String language){
		
		String QUERY_DISTRICT_BY_CITY_KEY = "select * where name contains '%s' and language = '%s'"; 
		
		return districtClient.getDistrictsByQuery(String.format(QUERY_DISTRICT_BY_CITY_KEY, keyWord, language), LIMIT).getEntities();
	}
	
	public List<DistrictCollection> getDistrictByUuid(final String token, final String uuid){
		
		String QUERY_DISTRICT_BY_UUID = "select * where uuid = '%s'"; 
		
		return districtClient.getDistrictsByQuery(String.format(QUERY_DISTRICT_BY_UUID, uuid), LIMIT).getEntities();
	}
	
	public List<DistrictCollection> getDistrictByCityKey(final String token, final String cityKey, final String lang){
		
		String QUERY_DISTRICT_BY_KEY = "select * where city_key = '%s' and language = '%s' order by sequence"; 
		
		return districtClient.getDistrictsByQuery(String.format(QUERY_DISTRICT_BY_KEY, cityKey, lang), LIMIT).getEntities();
	}
	
	public List<DistrictEntity> getDistrictByCityKey(final String cityKey, final String lang) {
		try {
			return districtRepo.findByCityKeyAndLanguageOrderBySequenceAsc(cityKey, lang);
		} catch (Exception e) {
			LOG.error("[getDistrictByCityKey] error detail: {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
