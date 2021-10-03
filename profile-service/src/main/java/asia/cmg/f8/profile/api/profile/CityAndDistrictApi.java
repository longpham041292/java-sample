package asia.cmg.f8.profile.api.profile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.database.entity.CityEntity;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.domain.repository.CityRepository;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;
import asia.cmg.f8.profile.dto.CityDTO;

@RestController
public class CityAndDistrictApi {

	@Autowired
	CityRepository cityRepo;
	
	@Autowired
	DistrictRepository districtRepo;
	
	@GetMapping(value = "/mobile/v1/city", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllCitiesByLanguage(@RequestParam(name = "language", required = false, defaultValue = "en") final String language, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<CityEntity> cityEntities = cityRepo.findByLanguageOrderBySequenceAsc(language);
			List<CityDTO> cityList = cityEntities.stream()
									.map(entity -> this.convert(entity))
									.filter(city -> Objects.nonNull(city))
									.collect(Collectors.toList());
			apiResponse.setData(cityList);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/mobile/v1/district", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllDistrictsByCityKeyAndLanguage(@RequestParam(name = "language", required = false, defaultValue = "en") final String language,
																	@RequestParam(name = "cityKey") final String cityKey,
																	final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<DistrictEntity> cityEntities = districtRepo.findByCityKeyAndLanguageOrderBySequenceAsc(cityKey, language);
			apiResponse.setData(cityEntities);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	private CityDTO convert(CityEntity entity) {
		if(entity != null) {
			return CityDTO.builder()
					.key(entity.getKey())
					.name(entity.getName())
					.language(entity.getLanguage())
					.sequence(entity.getSequence())
					.latitude(entity.getLatitude())
					.longitude(entity.getLongtitude())
					.build();
		}
		
		return null;
	}
}
