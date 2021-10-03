package asia.cmg.f8.notification.service.session;

import asia.cmg.f8.notification.push.PushMessage;

import java.util.Objects;

/**
 * Created on 4/18/17.
 */
final class SessionPushMessage extends PushMessage {

    private final String userUuid; // uuid of receiver
    private final String distinctKey; // just used to compare

    public SessionPushMessage(final String type, final String userUuid, final String distinctKey) {
        super(type);
        this.userUuid = userUuid;
        this.distinctKey = distinctKey;
    }

    @Override
    public boolean equals(final Object message) {
        if (this == message) {
            return true;
        }
        if (message == null || getClass() != message.getClass()) {
            return false;
        }

        final SessionPushMessage that = (SessionPushMessage) message;
        if (!userUuid.equals(that.userUuid)) {
            return false;
        }
        return distinctKey.equals(that.distinctKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUuid, distinctKey);
    }

    public String getUserUuid() {
        return userUuid;
    }
}
