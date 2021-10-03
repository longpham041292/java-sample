package asia.cmg.f8.common.dto;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("squid:S2384")
public class GroupEntity {
	@JsonProperty("path")
    @Nullable
    private String path;

    @JsonProperty("title")
    @Nullable
    private String title;

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(@Nullable final String path) {
        this.path = path;
    }
    
    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable final String title) {
        this.title = title;
    }
}
