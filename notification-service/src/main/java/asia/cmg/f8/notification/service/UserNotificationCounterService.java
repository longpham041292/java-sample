package asia.cmg.f8.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.notification.entity.UserNotificationCounterEntity;
import asia.cmg.f8.notification.repository.UserNotificationCounterRepository;

@Service
public class UserNotificationCounterService {
	@Autowired
	private UserNotificationCounterRepository userNotificationCounterRepository;
	
	public UserNotificationCounterEntity findByUserUuid(String uuid) {
		return userNotificationCounterRepository.findOneByUserUuid(uuid);
	}
	
	public long getOffSetByUserUuid(String uuid) {
		UserNotificationCounterEntity entity = findByUserUuid(uuid);
		return entity == null ? 0 : entity.getOffset();
	}
	
	public UserNotificationCounterEntity updateOffsetForUser(String uuid, long offset) {
		UserNotificationCounterEntity entity = findByUserUuid(uuid);
		if(entity == null) {
			entity = new UserNotificationCounterEntity();
			entity.setUserUuid(uuid);
			entity.setOffset(offset);
		} else {
			entity.setOffset(offset);
		}
		userNotificationCounterRepository.save(entity);
		return entity;
	}
}
