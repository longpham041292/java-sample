package asia.cmg.f8.notification.dto;

import asia.cmg.f8.notification.entity.InboxMessageType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by on 1/4/17.
 */
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
@Value.Style(
        typeImmutable = "*",
        visibility = Value.Style.ImplementationVisibility.PUBLIC,
        passAnnotations = {JsonIgnoreProperties.class, JsonInclude.class})
public interface AbstractInboxMessage {
    @JsonProperty("uuid")
    String getId();

    @JsonProperty("created_date")
    Long getCreatedDate();

    @JsonProperty("message_type")
    InboxMessageType getMessageType();

    @JsonProperty("description")
    @Nullable
    String getDescription();

    @JsonProperty("message_content")
    @Nullable
    Map<String, Object> getMessageContent();

    @JsonProperty("sender")
    InboxSender getSender();

    @JsonProperty("read")
    Boolean isRead();

    @JsonProperty("session_burned")
    @Nullable
    Integer getSessionBurned();

    @JsonProperty("total_session")
    @Nullable
    Integer getTotalSession();
    
    @JsonProperty("client_info")
    @Nullable
    List<QuestionResponse> getClientInfo();

}
