package asia.cmg.f8.profile.api.profile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.domain.entity.home.HomeSectionEntity;
import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;
import asia.cmg.f8.profile.domain.repository.HomeSectionRepository;
import asia.cmg.f8.profile.domain.repository.TrendingActionRepository;

@RestController
public class PublicOutsideApi {

	@Autowired
	private TrendingActionRepository trendingActionRepo;
	
	@Autowired
	private HomeSectionRepository homeSectionRepo;
	
	private static final Logger LOG = LoggerFactory.getLogger(PublicOutsideApi.class);
	
	@GetMapping(value = "/public/v1/trending_actions", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getTrendingActions() {
		ApiRespObject<Object>  apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<TrendingEventActionEntity> trendingActions = trendingActionRepo.findAll();
			apiResponse.setData(trendingActions);
		} catch (Exception e) {
			LOG.error("[getTrendingActions] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/public/v1/home_sections", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getHomeSections() {
		ApiRespObject<Object>  apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<HomeSectionEntity> homeSections = homeSectionRepo.findAll();
			apiResponse.setData(homeSections);
		} catch (Exception e) {
			LOG.error("[getHomeSections] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
