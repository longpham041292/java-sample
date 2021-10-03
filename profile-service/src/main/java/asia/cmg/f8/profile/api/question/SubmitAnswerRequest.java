package asia.cmg.f8.profile.api.question;

import java.util.List;
import java.util.Set;

/**
 * Created by tri.bui on 10/21/16.
 */
@SuppressWarnings("squid:S2384")
public class SubmitAnswerRequest {
    private String questionId;
    private Set<String> optionKeys;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(final String questionId) {
        this.questionId = questionId;
    }

    public void setOptionKeys(final Set<String> optionKeys) {
        this.optionKeys = optionKeys;
    }

    public Set<String> getOptionKeys() {
        return optionKeys;
    }
}
