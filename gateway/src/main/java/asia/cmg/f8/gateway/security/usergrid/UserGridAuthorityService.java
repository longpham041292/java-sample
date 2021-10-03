package asia.cmg.f8.gateway.security.usergrid;

import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.auth.TokenAuthentication;
import asia.cmg.f8.gateway.security.api.UserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static asia.cmg.f8.gateway.SecurityUtil.toBearer;
import static java.util.stream.Collectors.toList;

/**
 * Created on 12/27/16.
 */
public class UserGridAuthorityService implements AuthorityService {

    private final UserRoleApi roleApi;

    public UserGridAuthorityService(final UserRoleApi roleApi) {
        this.roleApi = roleApi;
    }

    @Override
    public TokenAuthentication loadAuthorities(final TokenAuthentication authentication) {

        final UserDetail userDetail = authentication.getUserDetail();
        if (userDetail != null) {
            final String uuid = userDetail.getUuid();
            final String accessToken = authentication.getToken();

            if (!isEmpty(uuid) && !isEmpty(accessToken)) {
                return authentication.createWith(loadAuthorities(uuid, toBearer(accessToken)));
            }
        }
        return authentication;
    }

    private List<GrantedAuthority> loadAuthorities(final String uuid, final String accessToken) {
        final UserGridResponse<UserRoleApi.RoleEntity> roles = roleApi.getRoles(uuid);
        if (roles != null && roles.getEntities() != null) {
            return roles.getEntities().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(toList());
        }
        return Collections.emptyList();
    }

    private boolean isEmpty(final String value) {
        return value == null || value.isEmpty();
    }
}
