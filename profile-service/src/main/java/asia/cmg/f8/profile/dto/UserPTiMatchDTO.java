package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import asia.cmg.f8.profile.database.entity.UserPtiMatchEntity;

public class UserPTiMatchDTO {

	private Long id = 0L;
	
	@JsonProperty("eu_uuid")
	private String euUuid = "";
	
	@JsonProperty("pt_uuid")
	private String ptUuid = "";
	
	private Double personality = 0.0;
	
	// Should be remove after public to store V2.0.0
	@JsonProperty("trainingStyle")
	private Double trainingStyle = 0.0;
	
	@JsonProperty("training_style")
	private Double trainingStyle_V2 = 0.0;
	
	private Double interest = 0.0;
	
	private Double average = 0.0;

	public UserPTiMatchDTO() {
	}

	public UserPTiMatchDTO(UserPtiMatchEntity entity) {
		if(entity != null) {
			this.id = entity.getId();
			this.euUuid = entity.getEuUuid();
			this.ptUuid = entity.getPtUuid();
			this.average = entity.getAverage();
			this.personality = entity.getPersonality();
			this.trainingStyle = entity.getTrainingStyle();
			this.trainingStyle_V2 = entity.getTrainingStyle();
			this.interest = entity.getInterest();
		}
	}
	
	public UserPTiMatchDTO(String euUuid, String ptUuid, Double personality, Double trainingStyle, Double interest,
			Double average) {
		this.euUuid = euUuid;
		this.ptUuid = ptUuid;
		this.personality = personality;
		this.trainingStyle = trainingStyle;
		this.interest = interest;
		this.average = average;
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

	public Double getTrainingStyle_V2() {
		return trainingStyle_V2;
	}

	public void setTrainingStyle_V2(Double trainingStyle_V2) {
		this.trainingStyle_V2 = trainingStyle_V2;
	}
}
