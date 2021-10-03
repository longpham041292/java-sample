package asia.cmg.f8.common.security.internal;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.util.SecurityConstants;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created on 11/1/16.
 */
public final class DefaultAccount implements Account {

    private final String accountUuid;
    private final String accessToken;
    private final String accountType;
    private final String accountLanguage;
    private final Collection<GrantedAuthority> authorities;

    public DefaultAccount(final String uuid, final String accessToken, final String type, final String language, final Collection<GrantedAuthority> authorities) {
        this.accountUuid = uuid;
        this.accessToken = accessToken;
        this.accountType = type;
        this.accountLanguage = language;
        this.authorities = authorities;
    }

    @Override
    public String uuid() {
        return accountUuid;
    }

    @Override
    public String ugAccessToken() {
        return accessToken;
    }

    @Override
    public String type() {
        return accountType;
    }

    @Override
    public String language() {
        return accountLanguage;
    }

    @Override
    public boolean isAdmin() {
        return authorities != null && authorities.stream().filter(authority -> SecurityConstants.ADMIN_ROLE.equalsIgnoreCase(authority.getAuthority())).findFirst().map(auth -> Boolean.TRUE).orElse(Boolean.FALSE);
    }
}
