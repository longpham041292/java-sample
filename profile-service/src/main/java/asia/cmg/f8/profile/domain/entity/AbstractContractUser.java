package asia.cmg.f8.profile.domain.entity;

import asia.cmg.f8.common.spec.view.ContractUserView;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Created on 11/21/16.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@JsonSerialize(as = ContractUser.class)
@JsonDeserialize(as = ContractUser.class)
@SuppressWarnings({"CheckReturnValue", "PMD"})
public abstract class AbstractContractUser implements ContractUserView {
}
