package asia.cmg.f8.user.notification.client;



import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.notification.dto.NotificationResponseUserApi;
import asia.cmg.f8.user.notification.entity.LastLoadNotificationTimeUserApi;


@Component
public class NotificationClientFallbackUserApi implements NotificationClientUserApi {

	 @Override
    public UserGridResponse<LastLoadNotificationTimeUserApi> getLastNotificationLoadedTime(@PathVariable(NotificationClientUserApi.UUID_PARAM) final String userUuid, @PathVariable(QUERY_PARAM) final String query) {
        return null;
    }

    @Override
    public UserGridResponse<NotificationResponseUserApi> getLatest(@PathVariable(QUERY_PARAM) final String query, @PathVariable("limit") final int limit) {
        return null;
    }
	
}
