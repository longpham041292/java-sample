package asia.cmg.f8.gateway.security.usergrid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created on 11/3/16.
 */
@FeignClient(value = "roles", name = "roles", url = "${usergrid.baseUrl}", fallback = UserRoleApiFallback.class)
public interface UserRoleApi {

	String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
	String GET_ROLES_QUERY = "/users/{uuid}/roles?" + SECRET_QUERY;
	
    @RequestMapping(value = "/users/{uuid}/roles", method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<RoleEntity> getRoles(@RequestParam("uuid") final String uuid, @RequestHeader("Authorization") final String accessToken);
    
    @RequestMapping(value = GET_ROLES_QUERY, method = GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<RoleEntity> getRoles(@RequestParam("uuid") final String uuid);

    @JsonIgnoreProperties(ignoreUnknown = true)
    class RoleEntity {
        private String roleName;

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(final String roleName) {
            this.roleName = roleName;
        }
    }
}
