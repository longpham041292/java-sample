package asia.cmg.f8.notification.push;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created on 1/5/17.
 */
public interface NotificationEventStream {

    String DEVICE_REGISTERED_INPUT_CHANNEL = "deviceRegisteredInput";
    String DEVICE_REGISTERED_OUTPUT_CHANNEL = "deviceRegisteredOutput";
    String TRANSFER_SESSION_INPUT_CHANNEL = "pushTransferSessionNotificationInput";
    String USER_POST_STATUS_INPUT_CHANNEL = "userPostStatusInput";
    String USER_LIKE_POST_INPUT_CHANNEL = "likePostInput";
    String USER_COMMENT_POST_INPUT_CHANNEL = "commentPostInput";
    String USER_LIKE_COMMENT_INPUT_CHANNEL = "likeCommentInput";
    String ADMIN_APPROVED_DOC_INPUT_CHANNEL = "adminApprovedDocumentInput";
    String USER_FOLLOWING_INPUT_CHANNEL = "userFollowingInput";
    String WALLET_EVENT_INPUT_CHANNEL = "walletEventOutput";
    String SCHEDULE_EVENT_INPUT_CHANNEL = "scheduleEventOutput";
    String PUSHING_NOTIFICATION_EVENT_INPUT_CHANNEL = "pushingNotificationEventOutput";

    @Input(DEVICE_REGISTERED_INPUT_CHANNEL)
    SubscribableChannel deviceRegisteredSubscribeChannel();

    @Output(DEVICE_REGISTERED_OUTPUT_CHANNEL)
    MessageChannel deviceRegisteredMessageChannel();

    @Input(TRANSFER_SESSION_INPUT_CHANNEL)
    SubscribableChannel pushTransferSessionNotificationSubscribeChannel();

    @Input(USER_POST_STATUS_INPUT_CHANNEL)
    SubscribableChannel userPostStatusSubcribeChannel();

    @Input(USER_LIKE_POST_INPUT_CHANNEL)
    SubscribableChannel userLikePostSubcribeChannel();

    @Input(USER_COMMENT_POST_INPUT_CHANNEL)
    SubscribableChannel userCommentPost();

    @Input(USER_LIKE_COMMENT_INPUT_CHANNEL)
    SubscribableChannel userLikeComment();

    @Input(ADMIN_APPROVED_DOC_INPUT_CHANNEL)
    SubscribableChannel adminApprovedDocument();

    @Input(USER_FOLLOWING_INPUT_CHANNEL)
    SubscribableChannel userFollowing();

    @Input(WALLET_EVENT_INPUT_CHANNEL)
    SubscribableChannel walletEvent();

    @Input(SCHEDULE_EVENT_INPUT_CHANNEL)
    SubscribableChannel scheduleEvent();
    
    @Input(PUSHING_NOTIFICATION_EVENT_INPUT_CHANNEL)
    SubscribableChannel pushingNotificationEvent();
}
