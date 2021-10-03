package asia.cmg.f8.profile.domain.service;

import asia.cmg.f8.common.security.Account;

import java.util.List;
import java.util.Set;

/**
 * Created by tri.bui on 10/21/16.
 */
@SuppressWarnings("squid:S2384")
public class SubmitAnswerCommand {
    private final Account account;
    private final Set<String> optionKeys;
    private final String questionId;

    public SubmitAnswerCommand(final Account account, final String questionId,
                               final Set<String> optionIds) {
        this.account = account;
        this.questionId = questionId;
        this.optionKeys = optionIds;
    }

    public String getQuestionId() {
        return questionId;
    }

    public Set<String> getOptionKeys() {
        return optionKeys;
    }

    public Account getAccount() {
        return account;
    }
}
