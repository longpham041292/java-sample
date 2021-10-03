package asia.cmg.f8.common.spec.club;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created on 12/6/16.
 */
public interface Club {
    @JsonProperty("code")
    @Nullable
    String getCode();

    @JsonProperty("name")
    @Nullable
    String getName();

    @JsonProperty("address")
    @Nullable
    String getAddress();

    @JsonProperty("longitude")
    @Nullable
    Double getLongitude();

    @JsonProperty("latitude")
    @Nullable
    Double getLatitude();

    @JsonProperty("clubtype")
    @Nullable
    String getClubtype();
    
    @JsonProperty("city")
    @Nullable
    String getCity();
    
    @JsonProperty("location_icon")
    @Nullable
    String getLocationIcon();
    
    @JsonProperty("logo")
    @Nullable
    String getLogo();
}
