package asia.cmg.f8.profile.api.question;

import asia.cmg.f8.profile.domain.entity.Option;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by tri.bui on 10/24/16.
 */
@SuppressWarnings("squid:S2384")
public class QuestionResponse {
	@JsonIgnore
	private Integer id;
	
	@JsonIgnore
    private String uuid;
	
    private String questionType;
    private String key;
    private String title;
    private String description;
    private boolean required;
    private List<OptionResponse> options;
    private String sequence;
    private boolean answered;
    private int limitedUserSelection;

    public QuestionResponse(final QuestionEntity question) {
        this.uuid = question.getUuid();
        this.questionType = question.getQuestionType().name();
        this.key = question.getKey();
        this.title = question.getTitle();
        this.description = question.getDescription();
        this.required = question.isRequired();
        this.sequence = question.getSequence();
        this.options = question.getOptions().stream().map(OptionResponse::new).collect(Collectors.toList());
        this.limitedUserSelection = question.getLimitedUserSelection();
    }
    
    public QuestionResponse(final asia.cmg.f8.profile.database.entity.QuestionEntity question) {
        this.id = question.id;
        this.questionType = question.questionType.name();
        this.key = question.key;
        this.title = question.title;
        this.description = question.description;
        this.required = question.required;
        this.sequence = String.valueOf(question.sequence);
        
        List<OptionResponse> optionsResp = new ArrayList<OptionResponse>();
        question.getOptions().forEach(questionOption -> {
        	Option option = new Option();
        	option.setFiltered(questionOption.filtered);
        	option.setIconUrl(questionOption.iconUrl);
        	option.setKey(questionOption.key);
        	option.setOrder(questionOption.sequence);
        	option.setSequence(String.valueOf(questionOption.sequence));
        	option.setText(questionOption.text);
        	option.setType(questionOption.type);
        	option.setWeight(questionOption.weight);
        	
        	OptionResponse optionResp = new OptionResponse(option);
        	optionsResp.add(optionResp);
        });
        this.options = optionsResp;
        
        this.limitedUserSelection = question.limitedUserSelection;
    }

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

    public int getLimitedUserSelection() {
        return limitedUserSelection;
    }

    public void setLimitedUserSelection(final int limitedUserSelection) {
        this.limitedUserSelection = limitedUserSelection;
    }

	public void setOptions(List<OptionResponse> options) {
		this.options = options;
	}
}
