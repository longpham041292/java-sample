package asia.cmg.f8.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CounterUsersAnsweredQuestion {

	@JsonProperty("QuestionKey")
	private String questionKey;
	
	@JsonProperty("QuestionContent")
	private String questionContent;
	
	@JsonProperty("CountAnsweredByPT")
	private int countAnsweredByPT = 0;
	
	@JsonProperty("CountAnsweredByEU")
	private int countAnsweredByEU = 0;

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public String getQuestionKey() {
		return questionKey;
	}

	public void setQuestionKey(String questionKey) {
		this.questionKey = questionKey;
	}

	public int getCountAnsweredByPT() {
		return countAnsweredByPT;
	}

	public void setCountAnsweredByPT(int countAnsweredByPT) {
		this.countAnsweredByPT = countAnsweredByPT;
	}

	public int getCountAnsweredByEU() {
		return countAnsweredByEU;
	}

	public void setCountAnsweredByEU(int countAnsweredByEU) {
		this.countAnsweredByEU = countAnsweredByEU;
	}
}
