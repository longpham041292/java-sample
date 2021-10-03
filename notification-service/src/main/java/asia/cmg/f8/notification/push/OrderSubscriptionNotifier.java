package asia.cmg.f8.notification.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;
import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.CommonStringUtils;

@Component
public class OrderSubscriptionNotifier extends NotificationSender {

	private Logger LOGGER = LoggerFactory.getLogger(OrderSubscriptionNotifier.class);
	private String EU_CONFIRM_MESSAGE_CODE = "message.subscription.purchased.confirmed.eu";
	private String PT_CONFIRM_MESSAGE_CODE = "message.subscription.purchased.confirmed.pt";
	private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
	
	@Autowired
	private BasicUserInfoClient userInfoClient;
	
    public void sendOrderSubscriptionCompletedMessage(final OrderSubscriptionCompletedEvent event) {
    	List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
    	
        final String euId = String.valueOf(event.getEuUuid());
    	LOGGER.info("Push order subscription message completed successfull from user {}", euId);
    	
        final UserGridResponse<BasicUserInfo> euUserResponse = userInfoClient.findBasicUserInfo(euId);
        if(euUserResponse == null || euUserResponse.getEntities().size() == 0){
            LOGGER.error("User not found");
            return;
        }
    	final BasicUserInfo euEntity = euUserResponse.getEntities().get(0);
        
		final PushMessage pushEuMessage = new PushMessage("order subscription");
        pushEuMessage.setLocalizedMessage(EU_CONFIRM_MESSAGE_CODE);
        sendToUser(euId, pushEuMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.ORDER_SUBSCRIPTION.name());

        final String ptId = String.valueOf(event.getPtUuid());
        final PushMessage pushPtMessage = new PushMessage("order subscription");
        pushPtMessage.setLocalizedMessage(PT_CONFIRM_MESSAGE_CODE, CommonStringUtils.formatNameInNotification(euEntity.getName()));
        tagged_accounts.add(new SocialUserInfo(euEntity.getUuid(), euEntity.getName()));
        
        sendToUser(ptId, pushPtMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.ORDER_SUBSCRIPTION.name());
    }
    
}
