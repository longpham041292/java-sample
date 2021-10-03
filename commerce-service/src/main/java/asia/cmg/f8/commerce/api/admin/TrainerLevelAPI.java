package asia.cmg.f8.commerce.api.admin;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.dto.LevelDTO;
import asia.cmg.f8.commerce.entity.LevelEntity;
import asia.cmg.f8.commerce.service.LevelService;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import feign.Body;

@RestController
public class TrainerLevelAPI {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainerLevelAPI.class);
	@Autowired
	LevelService levelService;
	
	@GetMapping(value = "/admin/v1/trainer/level", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> getListPTLevel() {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		List<LevelEntity> levelEntity = levelService.getListLevelEntity();
		return new ResponseEntity<Object>(levelEntity, HttpStatus.OK);
	}
	
	@PutMapping(value = "/admin/v1/trainer/level", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> updateCommissionLevel(@RequestBody final LevelDTO levelDataRequest){
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			LevelEntity level = levelService.updateLevelEntity(levelDataRequest);
			apiResponse.setData(level);
		} catch (Exception e) {
			LOGGER.error("[TrainerLevelAPI] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
