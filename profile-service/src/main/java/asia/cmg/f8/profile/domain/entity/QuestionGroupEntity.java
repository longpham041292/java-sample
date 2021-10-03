package asia.cmg.f8.profile.domain.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionGroupEntity {

	@JsonProperty("group_name")
	private String groupName;
	
	@JsonProperty("group_weight")
	private Double groupWeight;
	
	@JsonProperty("questions")
	private List<QuestionWeight> questions;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	

	public Double getGroupWeight() {
		return groupWeight;
	}

	public void setGroupWeight(Double groupWeight) {
		this.groupWeight = groupWeight;
	}

	public List<QuestionWeight> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionWeight> questions) {
		this.questions = questions;
	}
}
