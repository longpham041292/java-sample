package asia.cmg.f8.profile.domain.entity;

/**
 * Created by tri.bui on 10/25/16.
 */
public class Option {
    private String key;
    private String text;
    private String type; // 'icon' or 'text'
    private String iconUrl;
    private String sequence;
    private boolean filtered;
    private Integer weight;
    private Integer order;
    
    public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
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

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(final boolean filtered) {
        this.filtered = filtered;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(final Integer weight) {
        this.weight = weight;
    }
}
