package asia.cmg.f8.user.notification.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.notification.dto.NotificationResponseUserApi;
import asia.cmg.f8.user.notification.entity.LastLoadNotificationTimeUserApi;




@FeignClient(value = "lastConversation", url = "${feign.url}", fallback = NotificationClientFallbackUserApi.class)
public interface NotificationClientUserApi {

	String UUID_PARAM = "uuid";
    String QUERY_PARAM = "query";
    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";

  
    @RequestMapping(method = GET, path = "/users/{uuid}?ql={query}&" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<LastLoadNotificationTimeUserApi> getLastNotificationLoadedTime(@PathVariable(UUID_PARAM) final String userUuid, @PathVariable(QUERY_PARAM) final String query);

    
    @RequestMapping(method = GET, path = "/notifications?query={query}&limit={limit}&" + SECRET_QUERY, produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<NotificationResponseUserApi> getLatest(@PathVariable(QUERY_PARAM) final String query, @PathVariable("limit") final int limit);
	
}
