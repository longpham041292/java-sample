package asia.cmg.f8.notification.dto;

/**
 * Created by nhieu on 8/9/17.
 */
public class ChatNotificationRequest {

    private String message;

    private String messageId;

    private ChatMessageType type;

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public ChatMessageType getType() {
        return type;
    }

    public void setType(final ChatMessageType type) {
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
