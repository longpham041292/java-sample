package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created on 1/17/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterValue {

    private int timestamp;
    private int value;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}
