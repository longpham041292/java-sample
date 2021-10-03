package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("squid:S2384")
public class Counter {

    @JsonProperty("name")
    @Nullable
    private String name;

    @JsonProperty("values")
    @Nullable
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
