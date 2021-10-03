package asia.cmg.f8.profile.domain.entity;


import javax.annotation.Nullable;
/**
 * Created on 11/2/16.
 */

import asia.cmg.f8.profile.config.BuiltEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@BuiltEntity
@JsonSerialize(as = Attribute.class)
@JsonDeserialize(as = Attribute.class)
@SuppressWarnings("CheckReturnValue")
public interface AbstractAttribute {

    @JsonProperty("category")
    String getCategory();

    @JsonProperty("key")
    String getKey();

    @JsonProperty("language")
    String getLanguage();

    @JsonProperty("value")
    String getValue();
    
    @JsonProperty("activated")
    @Nullable
    String getActivated();
    
    @JsonProperty("description")
    @Nullable
    String getDescription();
    
    @JsonProperty("value1")
    @Nullable
    String getValue1();
    
}
