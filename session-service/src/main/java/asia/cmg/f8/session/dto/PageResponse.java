package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/9/17.
 */
public class PageResponse<T> {

    @JsonProperty("count")
    private long count;

    @JsonProperty("entities")
    private List<T> entities;

    @JsonProperty("cursor")
    private String cursor;

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    public List<T> getEntities() {
        if(entities == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(entities);
    }

    public void setEntities(final List<T> entities) {
        this.entities = new ArrayList<>(entities);
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
