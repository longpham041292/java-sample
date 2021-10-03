package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 2/14/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Counter {
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Integer value;
    
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }
}
