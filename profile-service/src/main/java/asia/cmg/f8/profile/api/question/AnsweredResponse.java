package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.profile.domain.entity.AnswerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tung.nguyenthanh
 */
@SuppressWarnings("squid:S2384")
public class AnsweredResponse {
    private String answerId;
    private String questionId;
    private List<String> optionKeys;

    public AnsweredResponse(final AnswerEntity answerEntity) {
        this.answerId = answerEntity.getUuid();
        this.questionId = answerEntity.getQuestionId();
        this.optionKeys = new ArrayList<>(answerEntity.getOptionKeys());
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(final String questionId) {
        this.questionId = questionId;
    }

    public List<String> getOptionKeys() {
        return optionKeys;
    }

    public void setOptionKeys(final List<String> optionKeys) {
        this.optionKeys = optionKeys;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(final String answerId) {
        this.answerId = answerId;
    }
}
