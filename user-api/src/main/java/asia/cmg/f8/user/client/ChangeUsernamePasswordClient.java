package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.dto.ResetUserPassword;
import asia.cmg.f8.user.entity.ChangePassword;
import asia.cmg.f8.user.entity.ChangePasswordResponse;
import asia.cmg.f8.user.entity.ChangeUsername;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.notification.dto.UsergridChangeNameApi;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/5/17.
 */
@FeignClient(value = "users", url = "${feign.url}", fallback = ChangeUsernamePasswordFallback.class)
public interface ChangeUsernamePasswordClient {
    String QUERY = "query";
    String SECRET_QUERY = "client_id=${userapi.userGridClientId}&client_secret=${userapi.userGridClientSecret}";
    String PUT_CHANGE_PASSWORD = "/users/{uuid}/password?" + SECRET_QUERY;
    String PUT_CHANGE_USERNAME = "/users/{uuid}?" + SECRET_QUERY;
    String GET_USER = "/users?" + SECRET_QUERY + "&ql={query}";

    @RequestMapping(value = PUT_CHANGE_PASSWORD, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<ChangePasswordResponse> changePassword(@PathVariable("uuid") final String uuid,
                                                          @RequestBody final ChangePassword request);
    @RequestMapping(value = "/users/{uuid}/password?" + SECRET_QUERY, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<ChangePasswordResponse> resetUserPassword(@PathVariable("uuid") final String uuid,
                                                          @RequestBody final ResetUserPassword request);

    @RequestMapping(value = PUT_CHANGE_USERNAME, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> changeUsername(@PathVariable("uuid") final String uuid,
                                                @RequestBody final ChangeUsername request);
    
    @RequestMapping(value = PUT_CHANGE_USERNAME, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> changeName(@PathVariable("uuid") final String uuid,
                                            @RequestBody final UsergridChangeNameApi request);

    @RequestMapping(value = GET_USER, method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> getUserByUserName(@PathVariable(QUERY) final String query);
    
    
    @RequestMapping(value = PUT_CHANGE_USERNAME, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<UserEntity> changeUserCode(@PathVariable("uuid") final String uuid,
                                                @RequestBody final UserEntity request);

}
