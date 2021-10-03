package asia.cmg.f8.common.spec.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 11/9/16.
 */
public class Media {
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("upload_url")
    private String url;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    public String getCaption() {
        return caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
