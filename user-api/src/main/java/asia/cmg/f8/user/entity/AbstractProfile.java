package asia.cmg.f8.user.entity;

import asia.cmg.f8.common.spec.user.IProfile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 11/7/16.
 */
@Value.Immutable
@JsonSerialize(as = Profile.class)
@JsonDeserialize(builder = Profile.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        init = "with*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractProfile implements IProfile {

    @Override
    @Nullable
    @JsonProperty("localized_data")
    public abstract Map<Locale, Map<String, String>> getLocalizedData();

}
