package asia.cmg.f8.common.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedUserGridResponse<ENTITY> extends UserGridResponse<ENTITY> {
	private String cursor;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }
}
