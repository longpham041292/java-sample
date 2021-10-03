package asia.cmg.f8.notification.entity;

import asia.cmg.f8.common.util.UserGridResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S2384")
public class PagedResponse<T> extends UserGridResponse<T> {

    private String cursor;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

}
