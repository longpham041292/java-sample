package asia.cmg.f8.profile.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import asia.cmg.f8.common.spec.social.PostContentType;
import asia.cmg.f8.profile.dto.ResourceLink;
import asia.cmg.f8.profile.dto.SponsoredInfo;

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
        passAnnotations = {
                JsonIgnoreProperties.class,
                JsonInclude.class,
                JsonSerialize.class,
                JsonDeserialize.class})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(as = ActivityEntity.class)
@JsonDeserialize(builder = ActivityEntity.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AbstractActivityEntity {

    @JsonProperty("uuid")
    @Nullable
    String getUuid();

    @JsonProperty("verb")
    @Nullable
    ActivityVerbType getVerb();

    @JsonProperty("published")
    @Nullable
    Long getPublished();

    @JsonProperty("text")
    @Nullable
    String getText();

    @JsonProperty("content_type")
    @Nullable
    PostContentType getContentType();

    @JsonProperty("links")
    @Nullable
    List<ResourceLink> getLinks();

    @JsonProperty("thumbnail_image_link")
    @Nullable
    String getThumbnailImageLink();

    @JsonProperty("status")
    @Nullable
    PostStatusType getStatus();
    
    @JsonProperty("owner_id")
    @Nullable
    String getOwnerId();
    
    @JsonProperty("request_uuid")
    @Nullable
    String getRequestUuid();

    @JsonProperty("tagged_uuids")
    @Nullable
	List<String> getTaggedUuids();
    
    @JsonProperty("video_duration")
    @Nullable
    Integer getVideoDuration();
    
    @JsonProperty("sponsored")
    @Nullable
    SponsoredInfo getSponsoredInfo();
}
