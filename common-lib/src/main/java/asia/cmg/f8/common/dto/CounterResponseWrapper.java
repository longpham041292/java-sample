package asia.cmg.f8.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created on 12/21/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("squid:S2384")
public class CounterResponseWrapper<ENTITY> {

    private List<ENTITY> counters;

    public List<ENTITY> getCounters() {
        return counters;
    }

    public void setCounters(final List<ENTITY> counters) {
        this.counters = counters;
    }

}
