package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Usergrid counter response
 *
 * @param <ENTITY>
 * @author tung.nguyenthanh
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
