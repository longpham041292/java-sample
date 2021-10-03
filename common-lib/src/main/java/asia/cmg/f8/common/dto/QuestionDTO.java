package asia.cmg.f8.common.dto;

import java.io.Serializable;
import java.util.List;
import asia.cmg.f8.common.spec.user.UserType;

public class QuestionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private Integer id;
    private String questionType;
    private String key;
    private String title;
    private String description;
    private Boolean filtered;
    private Boolean required;
    private Boolean hide;
    private Integer weight;
    private List<QuestionOptionDTO> options;
    private Integer sequence;
    private UserType usedFor;
    private String tags;
    private Integer limitedUserSelection;
    private String language;
    
    public static class Builder {
    	private Integer id;
        private String questionType;
        private String key;
        private String title;
        private String description;
        private Boolean filtered;
        private Boolean required;
        private Boolean hide;
        private Integer weight;
        private List<QuestionOptionDTO> options;
        private Integer sequence;
        private UserType usedFor;
        private String tags;
        private Integer limitedUserSelection;
        private String language;
        
        private Builder() {
			// TODO Auto-generated constructor stub
		}
        
		public Builder setId(Integer id) {
			this.id = id;
			return this;
		}
		public Builder setQuestionType(String questionType) {
			this.questionType = questionType;
			return this;
		}
		public Builder setKey(String key) {
			this.key = key;
			return this;
		}
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}
		public Builder setFiltered(Boolean filtered) {
			this.filtered = filtered;
			return this;
		}
		public Builder setRequired(Boolean required) {
			this.required = required;
			return this;
		}
		public Builder setHide(Boolean hide) {
			this.hide = hide;
			return this;
		}
		public Builder setWeight(Integer weight) {
			this.weight = weight;
			return this;
		}
		public Builder setOptions(List<QuestionOptionDTO> options) {
			this.options = options;
			return this;
		}
		public Builder setSequence(Integer sequence) {
			this.sequence = sequence;
			return this;
		}
		public Builder setUsedFor(UserType usedFor) {
			this.usedFor = usedFor;
			return this;
		}
		public Builder setTags(String tags) {
			this.tags = tags;
			return this;
		}
		public Builder setLimitedUserSelection(Integer limitedUserSelection) {
			this.limitedUserSelection = limitedUserSelection;
			return this;
		}
		public Builder setLanguage(String language) {
			this.language = language;
			return this;
		}
		
		public QuestionDTO build() {
			QuestionDTO question = new QuestionDTO();
			question.description = description;
			question.filtered = filtered;
			question.hide = hide;
			question.id = id;
			question.key = key;
			question.language = language;
			question.limitedUserSelection = limitedUserSelection;
			question.options = options;
			question.questionType = questionType;
			question.required = required;
			question.sequence = sequence;
			question.tags = tags;
			question.title = title;
			question.usedFor = usedFor;
			question.weight = weight;
			
			return question;
		}
    }

	
    public static Builder builder() {
    	return new Builder();
    }

	public Integer getId() {
		return id;
	}

	public String getQuestionType() {
		return questionType;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getFiltered() {
		return filtered;
	}

	public Boolean getRequired() {
		return required;
	}

	public Boolean getHide() {
		return hide;
	}

	public Integer getWeight() {
		return weight;
	}

	public List<QuestionOptionDTO> getOptions() {
		return options;
	}

	public Integer getSequence() {
		return sequence;
	}

	public UserType getUsedFor() {
		return usedFor;
	}

	public String getTags() {
		return tags;
	}

	public Integer getLimitedUserSelection() {
		return limitedUserSelection;
	}

	public String getLanguage() {
		return language;
	}
}
