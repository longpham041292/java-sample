package asia.cmg.f8.notification.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.notification.dto.DeviceInfo;
import asia.cmg.f8.notification.dto.NotificationResponse;
import asia.cmg.f8.notification.entity.UserGridResponse;

/**
 * Created on 1/5/17.
 */
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public UserGridResponse<DeviceInfo> registerDevice(@RequestBody final Map<String, Object> deviceInfo, @PathVariable(UUID_PARAM) final String uuid) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> sendNotification2User(@RequestBody final List<Object> notifications, @PathVariable(NotificationClient.UUID_PARAM) final String userUuid) {
        return null;
    }

    @Override
    public UserGridResponse<LastLoadNotificationTime> getLastNotificationLoadedTime(@PathVariable(NotificationClient.UUID_PARAM) final String userUuid, @PathVariable(QUERY_PARAM) final String query) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> updateLastNotificationLoadedTime(@PathVariable(NotificationClient.UUID_PARAM) final String userUuid, @RequestBody final Map<String, Object> content) {
        return null;
    }

    @Override
    public UserGridResponse<NotificationResponse> getLatest(@PathVariable(QUERY_PARAM) final String query, @PathVariable("limit") final int limit) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> sendNotification2UserGroup(final List<Object> notifications, final String groupUuid) {
        return null;
    }
}
