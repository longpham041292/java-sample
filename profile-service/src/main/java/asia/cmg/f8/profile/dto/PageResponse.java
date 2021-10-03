package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 1/9/17.
 */
public class PageResponse<T> {

    @JsonProperty("count")
    private long count;

    @JsonProperty("items")
    private List<T> items;

    @JsonProperty("cursor")
    private String cursor;
    
    private int page;
    
    private int size;

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }

    public List<T> getItems() {
        if(items == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(items);
    }

    public void setItems(final List<T> items) {
        this.items = new ArrayList<>(items);
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
    
}
