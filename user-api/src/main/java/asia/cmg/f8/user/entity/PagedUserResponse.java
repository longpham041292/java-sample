package asia.cmg.f8.user.entity;

import asia.cmg.f8.common.util.UserGridResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedUserResponse<ENTITY> extends UserGridResponse<ENTITY> {

    private String cursor;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

}
