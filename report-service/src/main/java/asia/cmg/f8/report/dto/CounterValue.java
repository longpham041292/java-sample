package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CounterValue {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("value")
    private int value;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }


}
