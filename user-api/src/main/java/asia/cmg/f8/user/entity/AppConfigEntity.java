package asia.cmg.f8.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created by nhieu on 8/4/17.
 */
public class AppConfigEntity {

    @JsonProperty("name")
    @Nullable
    private String name;

    @JsonProperty("value")
    @Nullable
    private String value;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(final @Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(final @Nullable String value) {
        this.value = value;
    }
}

