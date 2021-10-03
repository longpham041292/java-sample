package asia.cmg.f8.profile.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClubOwnerInfoRequest {

	@JsonProperty("club_amenities")
	private List<String> clubAmenities;
	
	@JsonProperty("club_opening_hours")
	private String clubOpeningHours;

	public List<String> getClubAmenities() {
		return clubAmenities;
	}

	public void setClubAmenities(List<String> clubAmenities) {
		this.clubAmenities = clubAmenities;
	}

	public String getClubOpeningHours() {
		return clubOpeningHours;
	}

	public void setClubOpeningHours(String clubOpeningHours) {
		this.clubOpeningHours = clubOpeningHours;
	}
}
