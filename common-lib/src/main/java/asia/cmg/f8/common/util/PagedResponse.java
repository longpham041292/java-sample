package asia.cmg.f8.common.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponse<ENTITY> {
	private int count;

    private List<ENTITY> entities;

    public List<ENTITY> getEntities() {
        return entities;
    }
    
    public void setEntities(final List<ENTITY> entities) {
        this.entities = entities;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public boolean isEmpty() {
        return entities == null || entities.isEmpty();
    }
}
