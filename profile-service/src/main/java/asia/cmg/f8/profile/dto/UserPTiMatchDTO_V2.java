package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPTiMatchDTO_V2 {

	@JsonProperty("id")
	private Long id = 0L;
	
	@JsonProperty("eu_uuid")
	private String euUuid = "";
	
	@JsonProperty("pt_uuid")
	private String ptUuid = "";
	
	@JsonProperty("personality")
	private Double personality = 0d;
	
	@JsonProperty("training_style")
	private Double trainingStyle = 0d;
	
	@JsonProperty("interest")
	private Double interest = 0d;
	
	@JsonProperty("average")
	private Double average = 0d;
	
//	@JsonProperty("pt_level")
	@JsonIgnore
	private String ptLevel;
	
	@JsonIgnore
//	@JsonProperty("pt_booking_credit")
	private Integer ptBookingCredit = 0;

	public UserPTiMatchDTO_V2() {
		// TODO Auto-generated constructor stub
	}
	
	public UserPTiMatchDTO_V2(String euUuid, String ptUuid, 
			Double average, Double personality, Double trainingStyle, Double interest,  
			String ptLevel, Integer ptBookingCredit) {
		this.euUuid = euUuid;
		this.ptUuid = ptUuid;
		this.average = average;
		this.personality = personality;
		this.trainingStyle = trainingStyle;
		this.interest = interest;
		this.ptLevel = ptLevel;
		this.ptBookingCredit = ptBookingCredit;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEuUuid() {
		return euUuid;
	}

	public void setEuUuid(String euUuid) {
		this.euUuid = euUuid;
	}

	public String getPtUuid() {
		return ptUuid;
	}

	public void setPtUuid(String ptUuid) {
		this.ptUuid = ptUuid;
	}

	public Double getPersonality() {
		return personality;
	}

	public void setPersonality(Double personality) {
		this.personality = personality;
	}

	public Double getTrainingStyle() {
		return trainingStyle;
	}

	public void setTrainingStyle(Double trainingStyle) {
		this.trainingStyle = trainingStyle;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public String getPtLevel() {
		return ptLevel;
	}

	public void setPtLevel(String ptLevel) {
		this.ptLevel = ptLevel;
	}

	public Integer getPtBookingCredit() {
		return ptBookingCredit;
	}

	public void setPtBookingCredit(Integer ptBookingCredit) {
		this.ptBookingCredit = ptBookingCredit;
	}
}
