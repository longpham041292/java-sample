package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.profile.domain.entity.QuestionEntity;

public class QuestionManageResponse {
    private final String sequence;
    private final String uuid;
    private final String key;
    private final String title;
    private final boolean filtered;
    private final int weight;
    private final boolean hide;
    private final int limitedUserSelection;
    private final String questionType;

    public QuestionManageResponse(final asia.cmg.f8.profile.database.entity.QuestionEntity question) {
        super();
        this.sequence = String.valueOf(question.sequence);
        this.uuid = String.valueOf(question.id);
        this.key = question.key;
        this.title = question.title;
        this.filtered = question.filtered;
        this.weight = question.weight;
        this.hide = question.hide;
        this.limitedUserSelection = question.limitedUserSelection;
        this.questionType = question.questionType.name();
    }

    public String getSequence() {
        return sequence;
    }

    public String getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isHide() {
        return hide;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public int getLimitedUserSelection() {
        return limitedUserSelection;
    }

	public String getQuestionType() {
		return questionType;
	}
}
