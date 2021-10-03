package asia.cmg.f8.session.dto;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.session.entity.PopupScheduleCase;

import java.util.List;

/**
 * Created by nhieu on 8/15/17.
 */
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
public interface AbstractPopupSchedule {

    @JsonProperty("name")
    @Nullable
    String getName();
    
    @JsonProperty("picture")
    @Nullable
    String getPicture();
    
    @JsonProperty("popupScheduleCase")
    @Nullable
    PopupScheduleCase getPopupScheduleCase();

    @JsonProperty("startDate")
    @Nullable
    Long getStartDate();
    
    @JsonProperty("expiredDate")
    @Nullable
    Long getExpiredDate();
    
    @JsonProperty("totalSessions")
    @Nullable
    Integer getTotalSessions();
    
    @JsonProperty("burnedSessions")
    @Nullable
    Integer getBurnedSessions();
    
    @JsonProperty("ptUserName")
    @Nullable
    String getPtUserName();
    
    @JsonProperty("ptUuid")
    @Nullable
    String getPtUuid();

    @JsonProperty("trainerList")
    @Nullable
    List<ContractUser> getTrainerList();
}
