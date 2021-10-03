package asia.cmg.f8.profile.domain.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.spec.order.ImportUserResult;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;

import java.util.Set;

/**
 * Created on 1/20/17.
 */
@FeignClient(value = "sessions", url = "${feign.sessionUrl}", fallback = SessionClientFallbackImpl.class)
public interface SessionClient {
    String GET_CONTRACTING_USER = "/session/users/contracting";

    @RequestMapping(value = GET_CONTRACTING_USER, method = RequestMethod.GET)
    Set<String> getContractingOfUser(@RequestParam("user_uuid") final String currentUserId,
                                     @RequestParam("user_role") final String userRole,
                                     @RequestParam("users") final Collection<String> users);
    
    @RequestMapping(value = "/session/users/migrationExisted", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    ImportUserResult isValidUserCodeInMigrationDB(@RequestParam("userCode") final String  userCode, @RequestParam("userType") final String userType);
   
	
}
