package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created on 1/17/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Counter {

    private String name;
    private List<CounterValue> values;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<CounterValue> getValues() {
        return values;
    }

    public void setValues(final List<CounterValue> values) {
        this.values = values;
    }
}
