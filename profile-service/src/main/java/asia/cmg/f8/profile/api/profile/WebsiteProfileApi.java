package asia.cmg.f8.profile.api.profile;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.database.entity.CityEntity;
import asia.cmg.f8.profile.database.entity.DistrictEntity;
import asia.cmg.f8.profile.domain.entity.ShortUserEntity;
import asia.cmg.f8.profile.domain.repository.CityRepository;
import asia.cmg.f8.profile.domain.repository.DistrictRepository;
import asia.cmg.f8.profile.domain.service.PTInfoService;
import asia.cmg.f8.profile.domain.service.UserProfileService;
import asia.cmg.f8.profile.dto.CityDTO;
import asia.cmg.f8.profile.dto.PagedResponse;

@RestController
public class WebsiteProfileApi {

	@Autowired
	PTInfoService pTInfoService;
	
	@Autowired
	UserProfileService userProfileService;
	
	@Autowired
	DistrictRepository districtRepo;
	
	@Autowired
	CityRepository cityRepo;
	
	@GetMapping(value = "/public/web/v1/cities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllCitiesByLanguage(@RequestParam(name = "language", required = false, defaultValue = "en") final String language) {
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
	
	@GetMapping(value = "/public/web/v1/districts", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getAllDistrictsByCityKeyAndLanguage_PUBLIC(
					@RequestParam(name = "language", required = false, defaultValue = "en") final String language,
					@RequestParam(name = "cityKey") final String cityKey) {
		
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
	
	/**
	 * This is public api for getting activated Trainers by some criteria
	 * @return
	 */
	@GetMapping(value = "/public/web/v1/trainers/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchTrainerList(@PathVariable(name = "language") final String language,
													@RequestParam(name = "skills", required = false) Set<String> skills,
													@RequestParam(name = "level", required = false) String level,
													@RequestParam(name = "gender", required = false) String gender,
													@RequestParam(name = "districtKey", required = false) String districtKey,
													@PageableDefault(size = 12, page = 0) final Pageable pageable) {
		
		String SEARCH_TRAINERS = "SELECT su.uuid FROM session_users su";
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			// Check skills filtering
			if(!StringUtils.isEmpty(skills)) {
				skills = skills.stream().map(skill -> {
					return "'" + skill + "'";
				}).collect(Collectors.toSet());
				String skillList = String.join(",", skills);
				
				SEARCH_TRAINERS += " JOIN user_skills sk ON su.uuid = sk.user_uuid AND sk.skill_key IN (" + skillList + ")";
			}
				
			// Check districtKey filtering
			if(!StringUtils.isEmpty(districtKey)) {
				districtKey = "'" + districtKey + "'";
				SEARCH_TRAINERS += " JOIN user_district_location udl ON su.uuid = udl.user_uuid AND udl.district_key = " + districtKey;
			}
				
			// Build WHERE syntax
			SEARCH_TRAINERS +=" WHERE su.user_type = 'pt' AND su.activated = 1";
			
			if(!StringUtils.isEmpty(level)) {
				level = "'" + level + "'";
				SEARCH_TRAINERS += " AND su.level = " + level;
			}
				
			if(!StringUtils.isEmpty(gender)) {
				gender = gender.toUpperCase();
				switch (gender) {
					case "M":
						SEARCH_TRAINERS += " AND su.gender = 0";
						break;
					case "F":
						SEARCH_TRAINERS += " AND su.gender = 1";
						break;
					default:
						break;
				}
			}
			
			// Build ORDER BY syntax
			SEARCH_TRAINERS += " ORDER BY average_star DESC";
			
			// Build LIMIT syntax
			if(pageable.getPageNumber() > 0) {
				int offset = pageable.getPageSize() * pageable.getPageNumber();
				SEARCH_TRAINERS += " LIMIT " + offset + ", " + pageable.getPageSize();
			}else
				SEARCH_TRAINERS += " LIMIT " + pageable.getPageSize();
			
			List<Object> result = pTInfoService.getPTsByFilter(SEARCH_TRAINERS);
			
			// Mapping on UG
			PagedResponse<ShortUserEntity> pTsInfoResponse = new PagedResponse<ShortUserEntity>();
			if(!result.isEmpty()) {
				SEARCH_TRAINERS = "select * where userType = 'pt' and activated = true and status.document_status = 'approved' ";
				for(int i=0; i<result.size(); i++) {
					if(i == 0) {
						SEARCH_TRAINERS += " and (uuid='" + result.get(i) + "'";
					}else
						SEARCH_TRAINERS += " or uuid='" + result.get(i) + "'";
					if(i == result.size() - 1)
						SEARCH_TRAINERS += ")";
				}
				SEARCH_TRAINERS += " order by profile.rated desc";
				pTsInfoResponse = pTInfoService.convertPTsInfo(userProfileService.getPTsbyFilter(SEARCH_TRAINERS));
			}
			apiResponse.setData(pTsInfoResponse);
			apiResponse.setStatus(ErrorCode.SUCCESS);
			
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	/**
	 * This is public api for getting Trainer profile
	 * @return
	 */
	@GetMapping(value = "/public/web/v1/users/profile/{userUuid}/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTrainerProfile(@PathVariable(name = "language") final String language,
													@PathVariable(name = "userUuid") final String trainerUuid) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			// Collect data and set to apiResponse
			apiResponse.setData(userProfileService.getFullProfilePublic(trainerUuid, language));	
			apiResponse.setStatus(ErrorCode.SUCCESS);
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
