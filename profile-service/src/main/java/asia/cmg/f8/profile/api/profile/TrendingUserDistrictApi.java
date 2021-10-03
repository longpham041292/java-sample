package asia.cmg.f8.profile.api.profile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.profile.database.entity.TrendingUserDistrictEntity;
import asia.cmg.f8.profile.domain.repository.TrendingUserDistrictRepository;
import asia.cmg.f8.profile.dto.TrendingUserDistrictDTO;

@RestController
public class TrendingUserDistrictApi {

	@Autowired
	TrendingUserDistrictRepository trendingUserRepo;
	
	@GetMapping(value = "/admin/v1/trending_user_district", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getFixedTrendingUsers(final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<TrendingUserDistrictEntity> trendingUsers = trendingUserRepo.findAll();
			apiResponse.setData(trendingUsers);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/admin/v1/trending_user_district/city/{cityKey}/district/{districtKey}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getFixedTrendingUsersInDistrict(@PathVariable(name = "cityKey")final String cityKey,
																@PathVariable(name = "districtKey")final String districtKey,
																final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			List<TrendingUserDistrictEntity> trendingUsers = trendingUserRepo.findByCityKeyAndDistrictKey(cityKey, districtKey);
			apiResponse.setData(trendingUsers);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/admin/v1/trending_user_district", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> createFixedTrendingUser(@RequestBody final TrendingUserDistrictDTO request, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			TrendingUserDistrictEntity entity = new TrendingUserDistrictEntity();
			entity.setCityKey(request.getCityKey());
			entity.setDistrictKey(request.getDistrictKey());
			entity.setOrder(request.getOrder());
			entity.setUserType(request.getUserType());
			entity.setUserUuid(request.getUserUuid());
			entity = trendingUserRepo.saveAndFlush(entity);
			
			apiResponse.setData(entity);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/admin/v1/trending_user_district/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> updateFixedTrendingUser(@PathVariable(name = "id") final long id, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			TrendingUserDistrictEntity entity = trendingUserRepo.findOne(id);
			if(entity == null) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Id not found: " + id));
			} else {
				trendingUserRepo.delete(entity);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
