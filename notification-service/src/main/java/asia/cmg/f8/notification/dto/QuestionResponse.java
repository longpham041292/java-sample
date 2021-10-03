package asia.cmg.f8.notification.dto;


import asia.cmg.f8.notification.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by on 10/24/16.
 */
@SuppressWarnings("squid:S2384")
public class QuestionResponse {
    private String uuid;
    private String questionType;
    private String key;
    private String title;
    private String description;
    private boolean required;
    private List<OptionResponse> options;
    private String sequence;
    private boolean answered;

    public QuestionResponse() {
        // empty constructor
        this.options = new ArrayList<>();
    }

    public QuestionResponse(final QuestionEntity question) {
        this.uuid = question.getUuid();
        this.questionType = question.getQuestionType().name();
        this.key = question.getKey();
        this.title = question.getTitle();
        this.description = question.getDescription();
        this.required = question.isRequired();
        this.sequence = question.getSequence();
        this.options = question.getOptions().stream().map(OptionResponse::new).collect(Collectors.toList());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        this.sequence = sequence;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(final String questionType) {
        this.questionType = questionType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public List<OptionResponse> getOptions() {
        return options;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(final boolean answered) {
        this.answered = answered;
    }

    public void setOptions(final List<OptionResponse> options) {
        this.options = options;
    }
}
