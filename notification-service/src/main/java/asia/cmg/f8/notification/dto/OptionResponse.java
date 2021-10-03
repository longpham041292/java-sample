package asia.cmg.f8.notification.dto;

import asia.cmg.f8.notification.entity.Option;

/**
 * Created on 10/24/16.
 */
public class OptionResponse {
    private String key;
    private String sequence;
    private String text;
    private String type; // 'icon' or 'text'
    private String iconUrl;
    private boolean choose = false;

    public OptionResponse() {
        // empty constructor
    }

    public OptionResponse(final Option option) {
        this.key = option.getKey();
        this.sequence = option.getSequence();
        this.text = option.getText();
        this.type = option.getType();
        this.iconUrl = option.getIconUrl();
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        this.sequence = sequence;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(final String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(final boolean choose) {
        this.choose = choose;
    }

}
