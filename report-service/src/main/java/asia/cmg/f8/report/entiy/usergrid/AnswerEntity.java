package asia.cmg.f8.report.entiy.usergrid;

import asia.cmg.f8.common.spec.user.UserType;

import java.util.List;
import java.util.Set;

/**
 * Created on 10/14/16.
 */
@SuppressWarnings("squid:S2384")
public class AnswerEntity {
    private String uuid;
    private String questionId;
    private Set<String> optionKeys;
    private String owner;
    private UserType userType;
    private String eventId;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(final UserType userType) {
        this.userType = userType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(final String eventId) {
        this.eventId = eventId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "AnswerEntity [uuid=" + uuid + ", questionId=" + questionId
                + ", optionKeys=" + optionKeys + ", owner=" + owner
                + ", userType=" + userType + ", eventId=" + eventId + "]";
    }

}
