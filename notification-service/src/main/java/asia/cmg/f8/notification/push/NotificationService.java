package asia.cmg.f8.notification.push;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.ChatMessageType;
import asia.cmg.f8.notification.dto.ChatNotificationRequest;
import asia.cmg.f8.notification.dto.CustomDataEventName;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.PagedResponse;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by nhieu on 8/9/17.
 */
@Service
public class NotificationService extends NotificationSender {

    public static final String ORDER_TYPE = "order";
    public static final String MESSAGE_CHAT_IMAGE = "message.chat.image";
    public static final String MESSAGE_CHAT_VIDEO = "message.chat.video";
    public static final String MESSAGE_CHAT_TEXT = "message.chat.text";
    public static final String ORDER_MESSAGE_TEXT = "message.session.confirmed.order";
    public static final String POST_TYPE = "post";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    //private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final int DEFAULT_LIMIT = 1_000;
    private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
	
    @Autowired
    private BasicUserInfoClient basicUserInfoClient;


    public void sendToUser(final Account account, final String userUuid, final ChatNotificationRequest chatNotificationRequest) {

        final Optional<BasicUserInfo> userInfo = find(account.uuid());
        String name = "";
        if (userInfo.isPresent()) {
            name = userInfo.get().getName();
        }
        final ChatMessageType chatMessageType = chatNotificationRequest.getType();
        final String message = String.format("%s: %s", name, chatNotificationRequest.getMessage());
        final long messageTime = chatNotificationRequest.getTime();
        final String messageId = chatNotificationRequest.getMessageId();
        final PushContentMessage chatPushMessage = new PushContentMessage(message, CHAT_TYPE);

        switch (chatMessageType) {
            case IMAGE:
                chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_IMAGE, name);
                break;
            case VIDEO:
                chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_VIDEO, name);
                break;
            default:
                if (message == null || message.isEmpty()) {
                    chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_TEXT, name);
                }
                break;
        }
        chatPushMessage.setSenderId(account.uuid(), userInfo);
        sendToUser(userUuid, chatPushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.CHAT.name());
        super.updateLastMsg(userUuid, account.uuid(), chatPushMessage, chatMessageType, messageTime, messageId);
    }

    public void sendToUser(final OrderCompletedEvent event) {

        final String uuid = event.getUserUuid().toString();
        if (StringUtils.isEmpty(uuid)) {
            LOGGER.error("User Id is missing");
            return;
        }
        Map<String, Object> customData = new HashMap<String, Object>();
        customData.put("user_uuid", event.getPtUuid().toString());
        customData.put("user_type", UserType.PT);
        customData.put("event_name", CustomDataEventName.PURCHASE_PACKAGE);
        
        final PushMessage pushMessage = new PushMessage(ORDER_TYPE);
        pushMessage.setLocalizedMessage(ORDER_MESSAGE_TEXT);
        pushMessage.setCustomData(customData);
        
        sendToUser(uuid, pushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.ORDER_COMLETED.name());
    }

    public void sendToUserGroup(final String uuid, final ChatNotificationRequest request) {
        final String message = request.getMessage();

        final PushContentMessage chatPushMessage = new PushContentMessage(message, CHAT_TYPE);
        final ChatMessageType chatMessageType = request.getType();
        switch (chatMessageType) {
            case IMAGE:
                chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_IMAGE);
                break;
            case VIDEO:
                chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_VIDEO);
                break;
            default:
                if (message == null || message.isEmpty()) {
                    chatPushMessage.setLocalizedMessage(MESSAGE_CHAT_TEXT);
                }
                break;
        }
        super.sendToGroup(uuid, chatPushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR);
    }


    public void getAllUser(final Account account) {

        String cursor = null;
        final String message = "You have received a new message from Kate - LEEP Admin!";
        final PushContentMessage chatPushMessage = new PushContentMessage(message, POST_TYPE);
        chatPushMessage.setCustomData(Collections.singletonMap("post_uuid", "028eb16f-c308-11e7-8a09-02420a010408"));

        try {
            PagedResponse<BasicUserInfo> response = basicUserInfoClient
                    .searchUsersWithCursor("select uuid,name,username where activated=true", DEFAULT_LIMIT, null);
            do {

                final List<BasicUserInfo> basicUserInfos = response.getEntities();

                for (final BasicUserInfo basicUserInfo : basicUserInfos) {
                    LOGGER.info("--------------------------------------");
                    LOGGER.info("------ uuid=" + basicUserInfo.getUuid());
                    LOGGER.info("--------------------------------------");

                    if (basicUserInfo.getUuid() != null
                            && !basicUserInfo.getUuid().equals("028eb16f-c308-11e7-8a09-02420a010408")) {
                    	sendToUser(basicUserInfo.getUuid(), chatPushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.LISA_ADMIN.name());
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100L);
                    } catch (final InterruptedException exception) {
                        LOGGER.error(exception.getMessage(), exception);
                    }
                }

                cursor = response.getCursor();

                response = basicUserInfoClient.searchUsersWithCursor("select uuid where activated=true", DEFAULT_LIMIT,
                        cursor);
            } while (null != cursor);

        } catch (Exception e) {
            LOGGER.info(e.toString());
        }

        LOGGER.info("----------- end ------------------");

    }

}
