package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.entity.UserEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 11/17/16.
 */
@FeignClient(value = "users", url = "${feign.url}")
public interface RoleClient {

    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String ASSIGN_ROLE = "/roles/{role}/users/{uuid}?" + SECRET_QUERY;

    @RequestMapping(value = ASSIGN_ROLE, method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> assignRole(@PathVariable("uuid") String uuid,
                                            @PathVariable("role") String role);
}
