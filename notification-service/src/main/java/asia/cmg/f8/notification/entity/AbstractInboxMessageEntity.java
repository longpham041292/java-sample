package asia.cmg.f8.notification.entity;

import asia.cmg.f8.notification.dto.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created on 1/3/17.
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
@JsonSerialize(as = InboxMessageEntity.class)
@JsonDeserialize(builder = InboxMessageEntity.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface AbstractInboxMessageEntity {

    @JsonProperty("uuid")
    @Nullable
    String getId();

    @JsonProperty("user_id")
    String getUserId();

    @JsonProperty("sender_id")
    String getSenderId();

    @JsonProperty("message_type")
    InboxMessageType getInboxMessageType();

    @JsonProperty("created_date")
    Long getCreatedDate();

    @JsonProperty("content")
    @Nullable
    Map<String, Object> getContent();

    @JsonProperty("read")
    Boolean isRead();

    @JsonProperty("package_id")
    String getPackageId();

    @JsonProperty("client_info")
    @Nullable
    List<QuestionResponse> getClientInfo();

}
