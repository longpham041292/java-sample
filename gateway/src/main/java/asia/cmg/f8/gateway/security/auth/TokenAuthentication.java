package asia.cmg.f8.gateway.security.auth;

import asia.cmg.f8.gateway.security.api.UserDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created on 10/20/16.
 */
@JsonIgnoreProperties(value = {"details", "authorities", "authenticated", "credentials", "principal", "name"})
public final class TokenAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = -4518786494354431081L;

    @JsonProperty(value = "access_token")
    private final String token;
    
    @JsonProperty(value = "refresh_token")
    private final String refreshToken;

    @JsonProperty(value = "expires_in")
    private final Long exp;

    @JsonProperty(value = "user")
    private final UserDetail userDetail;

    public TokenAuthentication(final String token, final int exp, final UserDetail userDetail) {
        this(Collections.emptyList(), token,"" ,exp, userDetail);
    }
    
    public TokenAuthentication(final String token, final String refreshToken, final Long exp, final UserDetail userDetail) {
        this(Collections.emptyList(), token, refreshToken, exp, userDetail);
    }


    private TokenAuthentication(final Collection<? extends GrantedAuthority> authorities, final String token, final String refreshToken, final long exp, final UserDetail userDetail) {
        super(authorities);
        this.token = token;
        this.refreshToken = refreshToken;
        this.exp = exp;
        this.userDetail = userDetail;
    }

    public TokenAuthentication createWith(final Collection<? extends GrantedAuthority> authorities) {
        return new TokenAuthentication(authorities, this.getToken(), this.getRefreshToken(), this.getExp(), this.getUserDetail());
    }

    @JsonProperty("roles")
    public List<String> getRoles() {
        return getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetail.getUuid();
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    
    public String getToken() {
        return token;
    }

    public Long getExp() {
        return exp;
    }

    @Override
    public boolean isAuthenticated() {
        return userDetail.isActivated();
    }

    @Override
    public void setAuthenticated(final boolean authenticated) {
        // do not allow to change empty
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }
}
