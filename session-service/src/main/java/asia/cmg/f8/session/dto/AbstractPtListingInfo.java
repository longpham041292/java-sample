package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;
import javax.annotation.Nullable;


/**
 * Created on 1/11/17.
 */
@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
public interface AbstractPtListingInfo {

    @JsonProperty("uuid")
    @Nullable
    String getUuid();

    @JsonProperty("name")
    @Nullable
    String getName();

    @JsonProperty("username")
    @Nullable
    String getUsername();

    @JsonProperty("avatar")
    @Nullable
    String getAvatar();

    @JsonProperty("city")
    @Nullable
    String getCity();

    @JsonProperty("country")
    @Nullable
    String getCountry();

    @JsonProperty("activated")
    @Nullable
    Boolean isActivated();

    @JsonProperty("join_date")
    @Nullable
    Long getJoinDate();

    @JsonProperty("document_approval_status")
    @Nullable
    String getDocumentStatus();

    @JsonProperty("level")
    @Nullable
    String getLevel();

    @JsonProperty("pt_revenue")
    @Nullable
    Double getPtRevenue();

    @JsonProperty("f8_revenue")
    @Nullable
    Double getF8Revenue();

    @JsonProperty("display_pt_revenue")
    @Nullable
    String getDisplayPtRevenue();

    @JsonProperty("display_f8_revenue")
    @Nullable
    String getDisplayF8Revenue();
    
    
    @JsonProperty("user_code")
    @Nullable
    String getUserCode();
    
    @JsonProperty("phone")
    @Nullable
    String getPhone();
}
