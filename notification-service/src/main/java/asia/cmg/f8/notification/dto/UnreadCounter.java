package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created on 1/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnreadCounter {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }
}
