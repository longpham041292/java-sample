package asia.cmg.f8.notification.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.dto.ApiRespListObject;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.notification.database.entity.DeviceEntity;
import asia.cmg.f8.notification.database.entity.DeviceGroupEntity;
import asia.cmg.f8.notification.dto.DeviceRequest;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.enumeration.EPhoneType;
import asia.cmg.f8.notification.service.DeviceService;

@RestController
public class DeviceApi {
	
	@Autowired
	private DeviceService deviceService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeviceApi.class);

	@GetMapping(value = "/mobile/v1/devices/group/{group_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getDevicesByGroupId(@PathVariable(name = "group_id") final int deviceGroupId,
													  @PageableDefault(page = 0, size = 1000) final Pageable pageable,
													  final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			PagedResponse<DeviceEntity> pagedDevices = deviceService.getDevicesByGroupId(deviceGroupId, pageable);
			apiResponse.setData(pagedDevices);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			LOGGER.error("[getDevicesByGroupId] failed with detail: {}", e.getMessage());
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/mobile/v1/devices/user/{user_uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getDevicesByUserUuid(@PathVariable("user_uuid") final String userUuid, final Account account) {
		ApiRespListObject<DeviceEntity> apiResponse = new ApiRespListObject<DeviceEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<DeviceEntity> devices = deviceService.getDevicesByUserUuid(userUuid);
			apiResponse.setData(devices);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/mobile/v1/device", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> registerDevice(@RequestBody DeviceRequest deviceRequest, final Account account) {
		ApiRespObject<DeviceEntity> apiResponse = new ApiRespObject<DeviceEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			EPhoneType phoneType = deviceService.checkDeviceType(deviceRequest.getType());
			if(phoneType == null) {
				apiResponse.setStatus(ErrorCode.REQUEST_INVALID.withDetail("Device type value is invalid"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			
			DeviceEntity oldEntity = deviceService.getDeviceByDeviceIdAndUserUuid(deviceRequest.getId(), account.uuid());
			if(oldEntity == null) {	// First time of registering
				DeviceEntity deviceEntity = deviceService.convert(deviceRequest, phoneType, account.uuid());
				deviceEntity = deviceService.registerDevice(deviceEntity);
				apiResponse.setData(deviceEntity);
			} else {	// Update activate this one
				oldEntity.setNotifierId(deviceRequest.getToken());
				DeviceEntity deviceEntity = deviceService.activateRegisteredDevice(oldEntity);
				apiResponse.setData(deviceEntity);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		} 
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/mobile/v1/device/{device_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> unregisterDevice(@PathVariable(name = "device_id") final String deviceId, final Account account) {
		ApiRespObject<DeviceEntity> apiResponse = new ApiRespObject<DeviceEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			DeviceEntity deviceEntity = deviceService.getDeviceByDeviceIdAndUserUuid(deviceId, account.uuid());
			if(deviceEntity != null) {
				deviceEntity = deviceService.deactivateRegisteredDevice(deviceEntity);
				apiResponse.setData(deviceEntity);
			} else {
				LOGGER.info("Device id {} of user {} not found", deviceId, account.uuid());
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Device id of user not found"));
			}
			
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		} 
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PutMapping(value = "/mobile/v1/device/{device_id}/group/{group_id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> addDeviceToGroup(@PathVariable("device_id") final long deviceId, @PathVariable("group_id") final int groupId, final Account account) {
		ApiRespObject<DeviceEntity> apiResponse = new ApiRespObject<DeviceEntity>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			DeviceEntity deviceEntity = deviceService.getDeviceById(deviceId);
			DeviceGroupEntity deviceGroupEntity = deviceService.getDeviceGroupById(groupId);
			if(deviceEntity == null || deviceGroupEntity == null) {
				apiResponse.setStatus(ErrorCode.REQUEST_INVALID.withDetail("Device id or group id is not existed"));
				return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
			}
			deviceEntity.setDeviceGroup(deviceGroupEntity);
			deviceEntity = deviceService.updateDevice(deviceEntity);
			
			apiResponse.setData(deviceEntity);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
