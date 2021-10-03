package asia.cmg.f8.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/5/17.
 */
public class DeviceRequest {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("device_type")
    private String type;

    @JsonProperty("token")
    private String token;

    public String getId() {
        return deviceId;
    }

    public void setId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
    
    public String toString() {
    	return String.format("{device_id: %s, device_type: %s, token: %s}", deviceId, type, token);
    }
}
