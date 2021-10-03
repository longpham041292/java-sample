package asia.cmg.f8.notification.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.notification.dto.DeviceInfo;
import asia.cmg.f8.notification.dto.NotificationResponse;
import asia.cmg.f8.notification.entity.UserGridResponse;

/**
 * Created on 1/5/17.
 */
@FeignClient(value = "notification", url = "${feign.url}", fallback = NotificationClientFallback.class)
public interface NotificationClient {

	String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
    String UUID_PARAM = "uuid";
    String QUERY_PARAM = "query";

    @RequestMapping(method = PUT, path = "/devices/{uuid}?" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<DeviceInfo> registerDevice(@RequestBody final Map<String, Object> deviceInfo, @PathVariable(UUID_PARAM) final String uuid);

    @RequestMapping(method = POST, path = "/users/{uuid}/notifications?" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<Map<String, Object>> sendNotification2User(@RequestBody final List<Object> notifications, @PathVariable(UUID_PARAM) final String userUuid);

    @RequestMapping(method = POST, path = "/groups/{uuid}/notifications?" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<Map<String, Object>> sendNotification2UserGroup(@RequestBody final List<Object> notifications, @PathVariable(UUID_PARAM) final String groupUuid);
    
    @RequestMapping(method = GET, path = "/users/{uuid}?ql={query}&" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<LastLoadNotificationTime> getLastNotificationLoadedTime(@PathVariable(UUID_PARAM) final String userUuid, @PathVariable(QUERY_PARAM) final String query);
    
    @RequestMapping(method = PUT, path = "/users/{uuid}?" + SECRET_QUERY)
    UserGridResponse<Map<String, Object>> updateLastNotificationLoadedTime(@PathVariable(UUID_PARAM) final String userUuid, @RequestBody final Map<String, Object> content);

    @RequestMapping(method = GET, path = "/notifications?query={query}&limit={limit}&" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<NotificationResponse> getLatest(@PathVariable(QUERY_PARAM) final String query, @PathVariable("limit") final int limit);
}
