package asia.cmg.f8.common.dto;

import java.io.Serializable;

public class QuestionOptionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String key;
	private String text;
	private String type; // 'icon' or 'text'
	private String iconUrl;
	private Integer sequence;
	private Boolean filtered;
	private Integer weight;
	
	public static class Builder {
		private Integer id;
		private String key;
		private String text;
		private String type; // 'icon' or 'text'
		private String iconUrl;
		private Integer sequence;
		private Boolean filtered;
		private Integer weight;
		
		private Builder() {
		}
		
		public Builder setId(Integer id) {
			this.id = id;
			return this;
		}
		public Builder setKey(String key) {
			this.key = key;
			return this;
		}
		public Builder setText(String text) {
			this.text = text;
			return this;
		}
		public Builder setType(String type) {
			this.type = type;
			return this;
		}
		public Builder setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
			return this;
		}
		public Builder setSequence(Integer sequence) {
			this.sequence = sequence;
			return this;
		}
		public Builder setFiltered(Boolean filtered) {
			this.filtered = filtered;
			return this;
		}
		public Builder setWeight(Integer weight) {
			this.weight = weight;
			return this;
		}
		public QuestionOptionDTO build() {
			QuestionOptionDTO option = new QuestionOptionDTO();
			option.filtered = filtered;
			option.iconUrl = iconUrl;
			option.id = id;
			option.key = key;
			option.sequence = sequence;
			option.text = text;
			option.type = type;
			option.weight = weight;
			return option;
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public Integer getId() {
		return id;
	}
	public String getKey() {
		return key;
	}
	public String getText() {
		return text;
	}
	public String getType() {
		return type;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public Integer getSequence() {
		return sequence;
	}
	public Boolean getFiltered() {
		return filtered;
	}
	public Integer getWeight() {
		return weight;
	}
}
