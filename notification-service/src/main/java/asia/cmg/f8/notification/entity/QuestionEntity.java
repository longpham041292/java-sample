package asia.cmg.f8.notification.entity;

import java.util.List;

/**
 * Created by on 10/21/16.
 */
@SuppressWarnings("squid:S2384")
public class QuestionEntity {
    private String uuid;
    private QuestionType questionType;
    private String key;
    private String title;
    private String description;
    private boolean filtered;
    private boolean required;
    private boolean hide;
    private int weight;
    private List<Option> options;
    private String sequence;
    private String usedFor;
    private String tags;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(final QuestionType questionType) {
        this.questionType = questionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(final boolean filtered) {
        this.filtered = filtered;
    }

    public String getUsedFor() {
        return usedFor;
    }

    public void setUsedFor(final String usedFor) {
        this.usedFor = usedFor;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(final List<Option> options) {
        this.options = options;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        this.sequence = sequence;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(final boolean hide) {
        this.hide = hide;
    }
}
