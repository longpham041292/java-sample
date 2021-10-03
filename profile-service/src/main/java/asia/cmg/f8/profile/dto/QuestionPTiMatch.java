package asia.cmg.f8.profile.dto;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuestionPTiMatch {
	
	@JsonProperty(value = "question_id")
    private String questionId;

    @JsonProperty(value = "weight")
    private int weight;

    @JsonProperty(value = "options")
    private Set<String> options;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Set<String> getOptions() {
		return options;
	}

	public void setOptions(Set<String> options) {
		this.options = options;
	}
}
