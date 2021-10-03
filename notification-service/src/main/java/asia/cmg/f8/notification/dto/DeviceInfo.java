package asia.cmg.f8.notification.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {

    public static final String NOTIFIER_NAME = "notifier_name";
    public static final String DEVICE_ID = "device_id";
    public static final String PHONE_TYPE = "phone_type";
    public static final String USER_UUID = "user_uuid";

    @JsonProperty("uuid")
    private String uuid;
    
    @JsonProperty("created")
    private Long created;
    
    @JsonProperty("modified")
    private Long modified;

    @JsonProperty(DEVICE_ID)
    private String deviceId;

    @JsonProperty(NOTIFIER_NAME)
    private String notifier;

    @JsonProperty(PHONE_TYPE)
    private String phoneType;

    @JsonProperty(USER_UUID)
    private String userUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(final String notifier) {
        this.notifier = notifier;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(final String phoneType) {
        this.phoneType = phoneType;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public final Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put(DEVICE_ID, deviceId);
        map.put(PHONE_TYPE, phoneType);
        map.put(USER_UUID, userUuid);
        map.put(NOTIFIER_NAME, notifier);
        map.put("uuid", deviceId);
        map.put("created", created);
        map.put("modified", modified);
        return map;
    }
}
