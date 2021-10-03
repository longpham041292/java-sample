package asia.cmg.f8.notification.push;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

/**
 * Re-present a basic structure of push message.
 * Created on 1/10/17.
 */
public class PushMessage {

    public static final Object[] EMPTY = new Object[]{};
    public static final String CUSTOM_DATA_TYPE_KEY = "data.type";
    public static final String CUSTOM_DATA_UUID_KEY = "data.uuid";

    private final String type;
    private Map<String, Object> customData;

    private String messageKey;
    private Object[] messageArgs;

    public PushMessage(final String type) {
        this.type = type;
    }

    public void setLocalizedMessage(final String code, final Object... args) {
        this.messageKey = code;
        this.messageArgs = args;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public void setCustomData(final Map<String, Object> customData) {
        this.customData = customData;
    }

    public String getType() {
        return type;
    }

    public final String getLocalizedMessage(final MessageSource messageSource, final Locale locale) {
        if (messageKey != null) {
            final Object[] args = messageArgs == null ? EMPTY : messageArgs;
            return messageSource.getMessage(messageKey, args, locale);
        }
        return getDefaultMessage();
    }

    public String getDefaultMessage(){
        return null;
    }
    
    public String getDefaultTitle(){
        return null;
    }
}
