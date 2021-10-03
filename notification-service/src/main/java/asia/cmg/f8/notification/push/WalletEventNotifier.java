package asia.cmg.f8.notification.push;

import asia.cmg.f8.commerce.WalletEvent;
import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
@EnableBinding(NotificationEventStream.class)
public class WalletEventNotifier extends NotificationSender {
    private static final SocialUserInfo NO_AVATAR = null;
    private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();
    public static final String WALLET_TYPE = "wallet";
    public static final String PURCHASE_PACKAGE_MESSAGE_KEY = "message.commerce.wallet.package.purchase";
    public static final String EXPIRED_PACKAGE_MESSAGE_KEY = "message.commerce.wallet.package.expire";
    public static final String UPGRADE_WALLET_MESSAGE_KEY = "message.commerce.wallet.upgrade";

    public static final Logger LOGGER = LoggerFactory.getLogger(WalletEventNotifier.class);

    @Autowired
    @Qualifier("walletEventConverter")
    private MessageConverter messageConverter;

    @StreamListener(NotificationEventStream.WALLET_EVENT_INPUT_CHANNEL)
    public void onEvent(final Message message) {
        WalletEvent event = (WalletEvent) messageConverter.fromMessage(message, WalletEvent.class);
        switch (ENotificationEventName.valueOf(event.getNotiType().toString())) {
            case PURCHASE_PACKAGE:
                pushPurchasePackageNoti(event);
                break;
            case EXPIRE_PACKAGE:
                pushExpiredPackageNoti(event);
                break;
            case UPGRADE_WALLET:
                upgradeWalletNoti(event);
                break;
        }
    }

    private void upgradeWalletNoti(WalletEvent event) {
        final PushMessage pushToClientMessage =
                createLocalizedMessageClient(UPGRADE_WALLET_MESSAGE_KEY, event.getPackageName());
        sendToUser(event.getUserUuid().toString(), pushToClientMessage,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    private void pushExpiredPackageNoti(WalletEvent event) {
        LocalDateTime expiredAt = DateTimeUtils.convertToLocalDateTime(event.getExpiredAt());
        final PushMessage pushToClientMessage =
                createLocalizedMessageClient(EXPIRED_PACKAGE_MESSAGE_KEY,
                        event.getExpiredAmount(),
                        DateTimeUtils.formatDatetime(expiredAt));
        sendToUser(event.getUserUuid().toString(), pushToClientMessage,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    public void pushPurchasePackageNoti(WalletEvent event) {
        final PushMessage pushToClientMessage =
                createLocalizedMessageClient(PURCHASE_PACKAGE_MESSAGE_KEY, event.getPackageName());
        sendToUser(event.getUserUuid().toString(), pushToClientMessage,
                NO_TAGGED_ACCOUNTS, NO_AVATAR, event.getNotiType().toString());
    }

    private PushMessage createLocalizedMessageClient(final String messageCode, final Object... args) {
        final PushMessage message = new PushMessage(WALLET_TYPE);
        message.setLocalizedMessage(messageCode, args);
        return message;
    }
}
