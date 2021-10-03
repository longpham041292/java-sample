package asia.cmg.f8.profile.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionWeight {

	@JsonProperty("question_key")
	private String questionKey;
	
	@JsonProperty("weight")
	private Double weight;
	
	public String getQuestionKey() {
		return questionKey;
	}
	
	public void setQuestionKey(String questionKey) {
		this.questionKey = questionKey;
	}
	
	public Double getWeight() {
		return weight;
	}
	
	public void setWeight(Double weight) {
		this.weight = weight;
	}
}
