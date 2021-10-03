package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/6/17.
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = ProfileClientFallbackImpl.class)
public interface ProfileClient {
    String SECRET_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    String PUT_USER = "/users/{uuid}?" + SECRET_QUERY;

    @RequestMapping(value = PUT_USER, method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> updateUserProfile(@PathVariable("uuid") final String uuid,
                                                          @RequestBody final UserEntity profile);
}
