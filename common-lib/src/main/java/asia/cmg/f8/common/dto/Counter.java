package asia.cmg.f8.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created on 12/21/16.
 */
@SuppressWarnings("squid:S2384")
public class Counter {
    @JsonProperty("name")
    @Nullable
    private String name;

    @JsonProperty("values")
    @Nullable
    private List<CounterValue> values;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable final String name) {
        this.name = name;
    }

    @Nullable
    public List<CounterValue> getValues() {
        return values;
    }

    public void setValues(@Nullable final List<CounterValue> values) {
        this.values = values;
    }
}
