package asia.cmg.f8.profile.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("squid:S2384")
public class PagedRatingSessionResponse<T> {

    @JsonProperty("cursor")
    private String cursor;

    @JsonProperty("content")
    private List<T> content;

    private int total;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(final List<T> content) {
        this.content = content;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }
}
