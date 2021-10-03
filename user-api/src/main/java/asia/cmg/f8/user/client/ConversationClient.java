package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.PagedUserResponse;
import asia.cmg.f8.user.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by on 11/9/16.
 */
@FeignClient(value = "conversations", url = "${feign.url}",
        fallback = ConversationClientFallback.class)
public interface ConversationClient {

    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String UUID = "uuid";
    String QUERY = "query";
    String LIMIT = "limit";
    String CURSOR = "cursor";
    String LOGIN_UUID = "loginUuid";

    @RequestMapping(value = "/users/{loginUuid}/conversation/users/{uuid}?" + SECRET_QUERY, method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> createConversationConnectionWith( 
    		@PathVariable(LOGIN_UUID) final String loginUuid,
    		@PathVariable(UUID) final String userUuid);

    @RequestMapping(value = "/users/me/conversation/users/{uuid}?" + SECRET_QUERY, method = RequestMethod.DELETE,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> deleteConversationConnectionWith(
    		@PathVariable(LOGIN_UUID) final String loginUuid,
    		@PathVariable(UUID) final String userUuid);

    @RequestMapping(value = "/users/{uuid}/conversation?limit={limit}&cursor={cursor}&" + SECRET_QUERY, method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    PagedUserResponse<UserEntity> getConversationConnection(
    		@PathVariable(UUID) final String userUuid,
            @RequestParam(value = CURSOR, required = false) final String cursor,
            @RequestParam(LIMIT) final int limit);
}
