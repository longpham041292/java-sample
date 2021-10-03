package asia.cmg.f8.profile.api.district;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.profile.database.entity.CityEntity;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.domain.entity.CityCollection;
import asia.cmg.f8.profile.domain.entity.DistrictCollection;
import asia.cmg.f8.profile.domain.service.CityService;
import asia.cmg.f8.profile.domain.service.DistrictService;

@RestController
public class DistrictApi {

	@Autowired
	DistrictService districtService;
	
	@Autowired
	CityService cityService;
	
	@RequestMapping(value = "/districts", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public List<DistrictCollection> searchDistricts(final Account account,
							@RequestParam(name = "key_word", required = true) final String keyWord,
							@RequestParam(name = "language", required = true) final String language) {
		
		return districtService.searchDistrictsByName(account.ugAccessToken(), keyWord, language);
	}
	
	@GetMapping(value = "/districts/city/{city_key}/language/{language}")
	public List<DistrictCollection> getDistrictsByCityKey(final Account account,
							@PathVariable(name = "city_key") final String cityKey,
							@PathVariable(name = "language") final String lang) {
		
		return districtService.getDistrictByCityKey(account.ugAccessToken(), cityKey, lang);
	}
	
	@RequestMapping(value = "/districts/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public List<DistrictCollection> getDistrictByUuid(final Account account, @PathVariable(name = "uuid", required = true) final String uuid) {
		
		return districtService.getDistrictByUuid(account.ugAccessToken(), uuid);
	}
	
	/**
	 * Get all cities and districts belong to
	 * @param lang
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/districts/language/{language}")
	public List<DistrictCityResponse> getAllCitiesAndDistricts(@PathVariable(name = "language") final String lang, final Account account) {
		
		List<DistrictCityResponse> response = new ArrayList<DistrictCityResponse>();
		
		List<CityEntity> cities = cityService.getCitiesByLanguage(lang);
		
		cities.forEach(city -> {
			List<DistrictEntity> districts = districtService.getDistrictByCityKey(city.getKey(), lang);
			
			DistrictCityResponse entity = new DistrictCityResponse();
			entity.setCity_key(city.getKey());
			entity.setDistricts(districts);
			entity.setLanguage(city.getLanguage());
			entity.setSequence(city.getSequence());
			
			response.add(entity);
		});
		
		return response;
	}
}
