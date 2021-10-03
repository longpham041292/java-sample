package asia.cmg.f8.common.spec.commerce;

import asia.cmg.f8.common.spec.Identifiable;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;


/**
 * Created on 11/11/16.
 */
public interface Level extends Identifiable {

    @JsonProperty("code")
    String getCode();

    @JsonProperty("text")
    @Nullable
    String getText();
}
