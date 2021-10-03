package asia.cmg.f8.common.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Mapping between User Grid json data and java entity
 * <p>
 * Created on 11/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S2384")
public class UserGridResponse<ENTITY> {

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
