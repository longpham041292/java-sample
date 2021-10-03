package asia.cmg.f8.user.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@FeignClient(value = "usersConfirm", url = "${feign.url}")
public interface UserConfirmClient {

    @RequestMapping(value = "/users/{uuid}/confirm?token={token}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    String confirmActivateUser(@PathVariable("uuid") final String uuid,
                               @PathVariable("token") final String token);
}
