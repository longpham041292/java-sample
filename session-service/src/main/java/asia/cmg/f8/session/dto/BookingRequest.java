package asia.cmg.f8.session.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/27/16.
 */
@SuppressWarnings("squid:S2384")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingRequest {

    @JsonProperty("reservations")
    @NotNull
    private Set<TimeSlot> listReservation;
    
    @JsonProperty("package_uuid")
    private String packageId;
    
    @JsonProperty("club_uuid")
    private String clubUuid;
    
    @JsonProperty("club_name")
    private String clubName;
    
    @JsonProperty("club_address")
    private String clubAddress;

    public Set<TimeSlot> getListReservation() {
        return listReservation;
    }

    public void setListReservation(final Set<TimeSlot> listReservation) {
        this.listReservation = listReservation;
    }

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getClubUuid() {
		return clubUuid;
	}

	public String getClubName() {
		return clubName;
	}

	public String getClubAddress() {
		return clubAddress;
	}

	public void setClubUuid(String clubUuid) {
		this.clubUuid = clubUuid;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public void setClubAddress(String clubAddress) {
		this.clubAddress = clubAddress;
	}
}
