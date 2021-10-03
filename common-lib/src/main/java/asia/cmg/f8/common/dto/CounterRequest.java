package asia.cmg.f8.common.dto;

import java.util.Map;

/**
 * Created on 12/23/16.
 */
public class CounterRequest {

    private long timestamp;
    private Map<String, Integer> counters;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Integer> getCounters() {
        return counters;
    }

    public void setCounters(final Map<String, Integer> counters) {
        this.counters = counters;
    }
}
