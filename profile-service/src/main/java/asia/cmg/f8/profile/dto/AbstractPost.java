package asia.cmg.f8.profile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import asia.cmg.f8.common.spec.social.PostContentType;
import org.immutables.value.Value;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Created on 12/21/16.
 */

@Value.Immutable
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
@SuppressWarnings("CheckReturnValue")
public interface AbstractPost {

    @JsonProperty("post_id")
    @Nullable
    String getPostId();

    @JsonProperty("content")
    @Nullable
    String getContent();

    @JsonProperty("content_type")
    @Nullable
    PostContentType getContentType();

    @JsonProperty("links")
    @Nullable
    List<ResourceLink> getLinks();

    @JsonProperty("thumbnail_image_link")
    @Nullable
    String getThumbnailImageLink();

    @JsonProperty("published")
    @Nullable
    Long getPublished();

    @JsonProperty("num_of_likes")
    @Nullable
    Integer getNumberOfLikes();

    @JsonProperty("num_of_comments")
    @Nullable
    Integer getNumberOfComments();
    
    @JsonProperty("num_of_views")
    @Nullable
    Integer getNumberOfViews();

    @JsonProperty("request_uuid")
    @Nullable
    String getRequestUuid();
    
    @JsonProperty("tagged_uuids")
    @Nullable
    List<String> getTaggedUuids();

    @JsonProperty("video_duration")
    @Nullable
    Integer getVideoDuration();
}
