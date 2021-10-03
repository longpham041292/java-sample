package asia.cmg.f8.notification.push;

import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.CommonStringUtils;
import asia.cmg.f8.session.TransferSessionPackageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Push notification to new and old PT when session package is transferred.
 * <p>
 * Created on 1/7/17.
 */
@Component
@EnableBinding(NotificationEventStream.class)
public class SessionTransferredNotifier extends NotificationSender {

    public static final Logger LOGGER = LoggerFactory.getLogger(SessionTransferredNotifier.class);
    private static final String QUERY_ALL_USER_INFO = "select uuid, name where %s";
    public static final String MESSAGE_SESSION_TRANSFER_OLDPT_MSG = "message.session.transfer.oldpt";
    public static final String MESSAGE_SESSION_TRANSFER_NEWPT_MSG = "message.session.transfer.newpt";
    public static final String MESSAGE_SESSION_TRANSFER_OWNER_MSG = "message.session.transfer.owner";
    public static final String SESSION_TYPE = "session";
    public static final String CONTRACT_TYPE = "contract";
    public static final String INBOX_TYPE = "inbox";
    public static final String CLIENT_TYPE = "client";

    @Autowired
    @Qualifier("transferSessionsEventConverter")
    private MessageConverter messageConverter;

    private final BasicUserInfoClient userInfoClient;

    public SessionTransferredNotifier(final BasicUserInfoClient userInfoClient) {
        this.userInfoClient = userInfoClient;
    }

    @StreamListener(NotificationEventStream.TRANSFER_SESSION_INPUT_CHANNEL)
    public void onEvent(final Message message) {

        final TransferSessionPackageEvent event = (TransferSessionPackageEvent)
                messageConverter.fromMessage(message, TransferSessionPackageEvent.class);

        if (event != null) {
            final String oldPtUuid = event.getOldTrainerUuid().toString();
            final String newPtUuid = event.getNewTrainerUuid().toString();
            final String clientUuid = event.getUserUuid().toString();
            List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();

            final UserGridResponse<BasicUserInfo> usersInfoResp =
                    userInfoClient.findBasicUserInfoByQuery(String.format(QUERY_ALL_USER_INFO, "uuid = '" + clientUuid + "' or uuid = '" + newPtUuid + "' or uuid = '" + oldPtUuid + "'"));
            if (Objects.isNull(usersInfoResp) || usersInfoResp.getEntities().size() < 3) {
                LOGGER.error("Ignored push notification, Could not found new PT {} or old PT {} or end-user {}", newPtUuid, oldPtUuid, clientUuid);
                return;
            }
            final Map<String, BasicUserInfo> userInfoMap = usersInfoResp
                    .getEntities()
                    .stream()
                    .collect(Collectors.toMap(BasicUserInfo::getUuid, basicUserInfo -> basicUserInfo));

            final String clientName = userInfoMap.get(clientUuid).getName();
            final String clientPicture = userInfoMap.get(clientUuid).getPicture();
            final PushMessage pushToOldPtMessage = createLocalizedMessageOld(MESSAGE_SESSION_TRANSFER_OLDPT_MSG, CommonStringUtils.formatNameInNotification(clientName));
            pushToOldPtMessage.setCustomData(Collections.singletonMap("user_uuid", clientUuid));
            tagged_accounts.add(new SocialUserInfo(clientUuid, clientName));
            SocialUserInfo clientAvatar = SocialUserInfo.builder().uuid(clientUuid).name(clientName).picture(clientPicture).build();
            sendToUser(oldPtUuid, pushToOldPtMessage, tagged_accounts, clientAvatar, ENotificationEventName.TRANSFER_SESSION.name());
            LOGGER.info("Pushed notification to old PT {} transfer sessions with client {}", oldPtUuid, clientName);
            
            final PushMessage pushToNewPtMessage = createLocalizedMessageNewPt(MESSAGE_SESSION_TRANSFER_NEWPT_MSG, CommonStringUtils.formatNameInNotification(clientName));
            pushToNewPtMessage.setCustomData(Collections.singletonMap("user_uuid", clientUuid));
            sendToUser(newPtUuid, pushToNewPtMessage, tagged_accounts, clientAvatar, ENotificationEventName.TRANSFER_SESSION.name());
            LOGGER.info("Pushed notification to new PT {} transfer sessions with client {}", newPtUuid, clientName);

            final String newPtName = userInfoMap.get(newPtUuid).getName();
            final String newPtPicture = userInfoMap.get(newPtUuid).getPicture();
            final String oldPtName = userInfoMap.get(oldPtUuid).getName();
            final PushMessage pushToClientMessage = createLocalizedMessageClient(MESSAGE_SESSION_TRANSFER_OWNER_MSG, 
									            		CommonStringUtils.formatNameInNotification(oldPtName), 
									            		CommonStringUtils.formatNameInNotification(newPtName));
            pushToClientMessage.setCustomData(Collections.singletonMap("newPt_uuid", newPtUuid));
            tagged_accounts.clear();
            tagged_accounts.add(new SocialUserInfo(oldPtUuid, oldPtName));
            tagged_accounts.add(new SocialUserInfo(newPtUuid, newPtName));
            SocialUserInfo newPtAvatar = SocialUserInfo.builder().uuid(newPtUuid).name(newPtName).picture(newPtPicture).build();
            sendToUser(clientUuid, pushToClientMessage, tagged_accounts, newPtAvatar, ENotificationEventName.TRANSFER_SESSION.name());
            LOGGER.info("Pushed notification to the Client {} remaining sessions has been transferred from PT %s to PT %s", clientUuid, oldPtName, newPtName);
        }
    }

    private PushMessage createLocalizedMessageOld(final String messageCode, final Object... args) {
        final PushMessage message = new PushMessage(CLIENT_TYPE);
        message.setLocalizedMessage(messageCode, args);
        return message;
    }
    
    private PushMessage createLocalizedMessageNewPt(final String messageCode, final Object... args) {
        final PushMessage message = new PushMessage(INBOX_TYPE);
        message.setLocalizedMessage(messageCode, args);
        return message;
    }
    
    private PushMessage createLocalizedMessageClient(final String messageCode, final Object... args) {
        final PushMessage message = new PushMessage(CONTRACT_TYPE);
        message.setLocalizedMessage(messageCode, args);
        return message;
    }
}
