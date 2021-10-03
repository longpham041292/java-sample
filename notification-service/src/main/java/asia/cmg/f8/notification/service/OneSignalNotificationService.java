package asia.cmg.f8.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import asia.cmg.f8.notification.config.NotifierProperties;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.enumeration.EPhoneType;
import asia.cmg.f8.notification.repository.NotificationRepository;

@Service(value = "NotificationEntityService")
public class OneSignalNotificationService {

	@Autowired
	NotificationRepository notificationRepo;
	
	public PagedResponse<NotificationEntity> getLatestNotifications(final String receiverUuid, final Pageable pageable) throws Exception {
		try {
			PagedResponse<NotificationEntity> pagedResponse = new PagedResponse<NotificationEntity>();
			Page<NotificationEntity> pagedResult = notificationRepo.getLatestByReceiver(receiverUuid, pageable);
			
			if(pagedResult != null) {
				pagedResponse.setCount(pagedResult.getContent().size());
				pagedResponse.setEntities(pagedResult.getContent());
			}
			return pagedResponse;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void clearNotificationHistory(final String receiver) throws Exception {
		try {
			notificationRepo.clearAllNotificationByReceiver(receiver);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public NotificationEntity save(NotificationEntity entity) throws Exception {
		try {
			return notificationRepo.saveAndFlush(entity);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static String getNotifierName(final EPhoneType phoneType, final NotifierProperties properties) {
		switch (phoneType) {
		case IOS:
			return properties.getiOsNotifier();
		case ANDROID:
			return properties.getAndroidNotifier();
		default:
			return null;
		}
    }
}
