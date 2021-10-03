package asia.cmg.f8.notification.api;

import asia.cmg.f8.common.spec.notification.NotificationType;
import asia.cmg.f8.notification.database.entity.NotificationEntity;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationResponseDTO {
    private NotificationEntity entity;
    @JsonProperty("avatar")
    public String avatar;

    @JsonProperty("type")
    public String type;

    @JsonProperty("message")
    public  String message;

    @JsonProperty("title")
    public String title;

    @JsonProperty("body")
    public String body;

    @JsonProperty("uuid")
    public String uuid;

    @JsonProperty("custom_data")
    public Map<String, Object> customData;

    @JsonProperty("tagged_accounts")
    public List<Map<String, Object>> taggedAccounts;

    @JsonProperty("created_date")
    public long createdDate;
    
    @JsonProperty("event_name")
    public String eventName;
    
    @JsonProperty("read")
    public boolean read;

    public NotificationResponseDTO(NotificationEntity entity) {
        this.entity = entity;
        this.customData = new HashMap<>();
        this.taggedAccounts = new ArrayList<>();
        build();
    }

    public void build() {
        this.avatar = entity.getAvatar();
        this.type = NotificationType.valueOf(entity.getEventName()).getType();
        this.eventName = entity.getEventName();
        this.message = entity.getContent();
        this.title = entity.getTitle();
        this.body = entity.getContent();
        this.uuid = entity.getUuid();
        this.createdDate = entity.getCreatedDate().toInstant().toEpochMilli();
        this.read = entity.isRead();

        JsonParser jsonParser = new JsonParser();
        if(entity.getCustomData() != null) {
            JsonElement customData = jsonParser.parse(entity.getCustomData());
            customData.getAsJsonObject().entrySet().forEach(entry -> {
                this.customData.put(entry.getKey(), entry.getValue().getAsString());
            });
        }

        if(entity.getTaggedAccounts() != null) {
            JsonArray jsonTaggedAccounts = jsonParser.parse(entity.getTaggedAccounts()).getAsJsonArray();
            jsonTaggedAccounts.forEach(jsonElement -> {
                Map<String, Object> taggedAccountMap = new HashMap<>();
                jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
                    taggedAccountMap.put(entry.getKey(), entry.getValue().getAsString());
                });
                this.taggedAccounts.add(taggedAccountMap);
            });
        }
    }
}
