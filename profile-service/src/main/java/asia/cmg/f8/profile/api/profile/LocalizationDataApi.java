package asia.cmg.f8.profile.api.profile;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespListObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.domain.entity.LocalizationEntity;
import asia.cmg.f8.profile.domain.service.LocalizationService;

@RestController
public class LocalizationDataApi {
	
	@Autowired
	private LocalizationService localizationService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalizationDataApi.class);

	@RequestMapping(value = "/mobile/v1/localization", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getByCategoryAndLanguage(@Valid @RequestParam(name = "category") final String category,
														@RequestParam(name = "language") final String language, final Account account) {
		LOGGER.info("[getByCategoryAndLanguage] logged request data: category[{}] and language[{}]", category, language);
		
		ApiRespListObject<LocalizationEntity> apiResp = new ApiRespListObject<LocalizationEntity>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		apiResp.setData(localizationService.getAllByCategoryAndLanguage(category, language));
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/localization/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> searchByCategoryAndLanguage(@Valid @RequestParam(name = "category") final String category,
														@RequestParam(name = "language") final String language,
														@RequestParam(name = "keyword") String keyword, final Account account) {
		LOGGER.info("[searchByCategoryAndLanguage] logged request data: keyword[{}] and category[{}] and language[{}]", keyword, category, language);
		
		ApiRespListObject<LocalizationEntity> apiResp = new ApiRespListObject<LocalizationEntity>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		apiResp.setData(localizationService.searchByCategoryAndLanguage(keyword, category, language));
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
}
