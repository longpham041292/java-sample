package asia.cmg.f8.session.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Created on 12/6/16.
 */
public class ClubEntity {
    @JsonProperty("uuid")
    @Nullable
    private String id;

    @JsonProperty("code")
    @Nullable
    private String code;

    @JsonProperty("club_name")
    @Nullable
    private String name;

    @JsonProperty("address")
    @Nullable
    private String address;

    @JsonProperty("longitude")
    @Nullable
    private Double longitude;

    @JsonProperty("latitude")
    @Nullable
    private Double latitude;
    
    @JsonProperty("clubtype")
    @Nullable
    private String clubtype;
    
    @JsonProperty("city")
    @Nullable
    private String city;
    
    @JsonProperty("location_icon")
    @Nullable
    private String locationIcon;
    
    @JsonProperty("logo")
    @Nullable
    private String logo;
    

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

	public String getClubtype() {
		return clubtype;
	}

	public void setClubtype(final String clubtype) {
		this.clubtype = clubtype;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getLocationIcon() {
		return locationIcon;
	}

	public void setLocationIcon(String locationIcon) {
		this.locationIcon = locationIcon;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}    
}
