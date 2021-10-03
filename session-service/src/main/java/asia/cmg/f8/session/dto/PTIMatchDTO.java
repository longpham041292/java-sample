package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PTIMatchDTO {

	@JsonProperty("personality")
	private Double personality = 0d;
	
	@JsonProperty("training_style")
	private Double trainingStyle = 0d;
	
	@JsonProperty("interest")
	private Double interest = 0d;
	
	@JsonProperty("average")
	private Double average = 0d;

	public PTIMatchDTO() {
		// TODO Auto-generated constructor stub
	}
	
	public PTIMatchDTO(Double p, Double t, Double i, Double average) {
		this.personality = i;
		this.trainingStyle = t;
		this.interest = i;
		this.average = average;
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
}
