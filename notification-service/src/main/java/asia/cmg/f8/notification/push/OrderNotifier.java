package asia.cmg.f8.notification.push;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.OrderCreditCompletedEvent;
import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.database.entity.BasicUserEntity;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.repository.BasicUserRepository;

@Component
public class OrderNotifier extends NotificationSender {

	private Logger LOGGER = LoggerFactory.getLogger(OrderNotifier.class);
	
	private static final String ORDER_CREDIT_COMPLETED_MESSAGE_CODE = "message.credit.confirmed.order";
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
	
	@Autowired
	private BasicUserRepository basicUserRepo;
	
	public void sendOrderCreditCompletedMessage(final OrderCreditCompletedEvent message) {
		
		final String ownerUuid = String.valueOf(message.getOwnerUuid());
    	LOGGER.info("[sendOrderCreditCompletedMessage] Push completed order credit message to user {}", ownerUuid);
    	
    	BasicUserEntity userInfo = basicUserRepo.findByUuid(ownerUuid);
        if(userInfo == null){
            LOGGER.error("[sendOrderCreditCompletedMessage] User uuid {} not found", ownerUuid);
            return;
        }
    	
        SocialUserInfo avatar = new SocialUserInfo(userInfo.getUuid(), userInfo.getFullName(), userInfo.getAvatar());
    	final PushMessage pushEuMessage = new PushMessage("order credit");
        pushEuMessage.setLocalizedMessage(ORDER_CREDIT_COMPLETED_MESSAGE_CODE, message.getCredit());
        sendToUser(ownerUuid, pushEuMessage, NO_TAGGED_ACCOUNTS, avatar, ENotificationEventName.ORDER_CREDIT.name());
	}
}
