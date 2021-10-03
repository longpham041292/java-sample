package asia.cmg.f8.common.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

public interface Identifiable {

    @JsonProperty("uuid")
    @Nullable
    String getId();
}
