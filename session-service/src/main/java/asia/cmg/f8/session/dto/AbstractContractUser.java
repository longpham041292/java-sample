package asia.cmg.f8.session.dto;

import org.apache.avro.reflect.Nullable;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.view.ContractUserView;

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
    @JsonProperty("order_number")
    @Nullable
    abstract String getOrderNumber();

    @JsonProperty("session_burned")
    @Nullable
    abstract Integer getSessionBurned();
    
    @JsonProperty("session_confirmed")
    @Nullable
    abstract Integer getSessionConfirmed();

    @JsonProperty("session_number")
    @Nullable
    abstract Integer getSessionNumber();

    @JsonProperty("bought_date")
    @Nullable
    abstract Long getBoughtDate();

    @JsonProperty("expired_date")
    @Nullable
    abstract Long getExpiredDate();
    
    @JsonProperty("package_uuid")
    @Nullable
    abstract String getPackageUuid();
    
    @JsonProperty("followed")
    @Nullable
    abstract Boolean isFollowed();
    
    @JsonProperty("user_level")
    @Nullable
    abstract String getUserLevel();
    
    @JsonProperty("training_style")
    @Nullable
    abstract String getTrainingStyle();
}
