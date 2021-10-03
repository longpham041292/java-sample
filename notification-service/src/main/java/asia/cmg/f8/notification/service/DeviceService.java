package asia.cmg.f8.notification.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import asia.cmg.f8.notification.config.NotifierProperties;
import asia.cmg.f8.notification.database.entity.DeviceEntity;
import asia.cmg.f8.notification.database.entity.DeviceGroupEntity;
import asia.cmg.f8.notification.dto.DeviceRequest;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.enumeration.EPhoneType;
import asia.cmg.f8.notification.repository.DeviceGroupRepository;
import asia.cmg.f8.notification.repository.DeviceRepository;

@Service
public class DeviceService {
	
	@Autowired
	private DeviceRepository deviceRepo;
	
	@Autowired
	private DeviceGroupRepository deviceGroupRepo;
	
	@Autowired
	private NotifierProperties notifierProps;
	
	private static final String DEFAULT_GROUP = "ef8fd92b-448d-11e9-a532-0a580a8200be";
	private static final String EU_GROUP = "b3acbf6e-4acb-11e9-a532-0a580a8200be";
	private static final String PT_GROUP = "bc6961b3-4acb-11e9-a532-0a580a8200be";
	
	private static final Logger LOG = LoggerFactory.getLogger(DeviceService.class);
	
	public PagedResponse<DeviceEntity> getDevicesByGroupId(final int deviceGroupId, final Pageable pageable) throws Exception {
		PagedResponse<DeviceEntity> pagedResp = new PagedResponse<DeviceEntity>();
		
		try {
			Page<DeviceEntity> pagedResult = deviceRepo.getDevicesByGroupId(deviceGroupId, pageable);
			if(pagedResult != null) {
				pagedResp.setCount(pagedResult.getContent().size());
				pagedResp.setEntities(pagedResult.getContent());
			}
			return pagedResp;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public DeviceGroupEntity getDeviceGroupById(final int groupId) {
		try {
			return deviceGroupRepo.getOne(groupId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public DeviceEntity getDeviceById(final long deviceId) {
		try {
			return deviceRepo.getOne(deviceId);
		} catch (Exception e) {
			return null;
		}
	}
	
	public DeviceEntity getDeviceByDeviceIdAndUserUuid(final String deviceId, final String userUuid) {
		try {
			return deviceRepo.getByDeviceIdAndUserUuid(deviceId, userUuid);
		} catch (Exception e) {
			return null;
		}
	}
	
	public DeviceEntity updateDevice(DeviceEntity entity) throws Exception {
		try {
			return deviceRepo.saveAndFlush(entity);
		} catch (Exception e) {
			LOG.error("Updating device entity failure: {}", e.getMessage());
			throw e;
		}
	}
	
	public DeviceEntity activateRegisteredDevice(DeviceEntity deviceEntity) throws Exception {
		try {
			deviceEntity.setActivated(Boolean.TRUE);
			return deviceRepo.saveAndFlush(deviceEntity);
		} catch (Exception e) {
			LOG.error("Activate device entity failure: {}", e.getMessage());
			throw e;
		}
	}
	
	public DeviceEntity deactivateRegisteredDevice(DeviceEntity deviceEntity) throws Exception {
		try {
			deviceEntity.setActivated(Boolean.FALSE);
			return deviceRepo.saveAndFlush(deviceEntity);
		} catch (Exception e) {
			LOG.error("Deactivate device entity failure: {}", e.getMessage());
			throw e;
		}
	}
	
	public DeviceEntity registerDevice(DeviceEntity deviceEntity) throws Exception {
		try {
			DeviceGroupEntity defaultGroup = deviceGroupRepo.getByUuid(DEFAULT_GROUP);
			deviceEntity.setDeviceGroup(defaultGroup);
			return deviceRepo.saveAndFlush(deviceEntity);
		} catch (Exception e) {
			LOG.error("Registering device entity failure: {}", e.getMessage());
			throw e;
		}
	}
	
	public List<DeviceEntity> getDevicesByUserUuid(final String userUuid) {
		try {
			List<DeviceEntity> devices = deviceRepo.getByUserUuid(userUuid);
			return devices;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public DeviceEntity convert(DeviceRequest deviceRequest, EPhoneType phoneType, final String userUuid) {
		DeviceEntity deviceEntity = new DeviceEntity();
		
		deviceEntity.setDeviceId(deviceRequest.getId());
		deviceEntity.setNotifierId(deviceRequest.getToken());
		deviceEntity.setNotifierName(OneSignalNotificationService.getNotifierName(phoneType, notifierProps));
		deviceEntity.setUserUuid(userUuid);
		deviceEntity.setPhoneType(phoneType);
		deviceEntity.setActivated(Boolean.TRUE);
		
		return deviceEntity;
	}
	
	public EPhoneType checkDeviceType(String type) {
		try {
			return EPhoneType.valueOf(type.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

	public void setDeactivateAllDeviceByUuidExceptOne(String uuid, long id) {
		deviceRepo.updateStatusOldDeviceToDeactiveExceptOne(uuid, id);
	}

	public List<DeviceEntity> getByActivatedTrueAndUserUuid(String userUuid) {
		try {
			return deviceRepo.getByActivatedTrueAndUserUuid(userUuid);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}
