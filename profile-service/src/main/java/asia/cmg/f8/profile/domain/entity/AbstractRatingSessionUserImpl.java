package asia.cmg.f8.profile.domain.entity;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import asia.cmg.f8.common.spec.view.RatingSessionUser;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract  class AbstractRatingSessionUserImpl implements RatingSessionUser {

	
}
