package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 2/7/17.
 */
@FeignClient(name = "userLanguage", url = "${feign.url}", fallback = UserLanguageClientFallback.class)
public interface UserLanguageClient {

    @RequestMapping(method = GET, path = "/users/{uuid}?ql=select uuid,language&client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<BasicUserInfo> findUserLanguage(@PathVariable("uuid") final String userUuid);
}
