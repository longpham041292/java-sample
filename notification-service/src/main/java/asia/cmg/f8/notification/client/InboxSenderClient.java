package asia.cmg.f8.notification.client;

/**
 * Created on 1/4/17.
 */

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.UserEntityImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import rx.Observable;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(value = "users", url = "${feign.url}", fallback = InboxSenderClientFallbackImpl.class)
public interface InboxSenderClient {
    String QUERY = "query";

    String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
    String QUERY_USER_INFO = "/users?" + SECRET_QUERY + "&ql={query}";

    @RequestMapping(value = QUERY_USER_INFO,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    UserGridResponse<UserEntityImpl> getUserInfoByQuery(@PathVariable(QUERY) final String query);

    @RequestMapping(value = QUERY_USER_INFO,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    Observable<UserGridResponse<UserEntityImpl>> getUserInfoByQueryObservable(
            @PathVariable(QUERY) final String query);

}
