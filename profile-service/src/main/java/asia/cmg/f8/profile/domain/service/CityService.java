package asia.cmg.f8.profile.domain.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.profile.database.entity.CityEntity;
import asia.cmg.f8.profile.domain.client.CityClient;
import asia.cmg.f8.profile.domain.entity.CityCollection;
import asia.cmg.f8.profile.domain.repository.CityRepository;

@Component
public class CityService {

	private Logger LOG = LoggerFactory.getLogger(CityService.class);
//	private final int LIMIT = 100;
	
//	@Autowired
//	CityClient cityClient;
	
	@Autowired
	CityRepository cityRepo;
	
//	public List<CityCollection> getAllCitiesByLanguage(final String token, final String lang) {
//		
//		String query = "select * where language = '%s' order by sequence";
//		
//		return cityClient.getCitiesByQuery(token, String.format(query, lang), LIMIT).getEntities();
//	}
	
	public List<CityEntity> getCitiesByLanguage(final String language) {
		try {
			return cityRepo.findByLanguageOrderBySequenceAsc(language);
		} catch (Exception e) {
			LOG.error("[getCitiesByLanguage] error detail: {}", e.getMessage());
			return Collections.emptyList();
		}
	}
}
