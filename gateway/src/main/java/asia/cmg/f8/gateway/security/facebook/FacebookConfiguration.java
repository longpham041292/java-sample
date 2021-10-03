package asia.cmg.f8.gateway.security.facebook;

import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.usergrid.UserGridApi;
import asia.cmg.f8.gateway.security.usergrid.UserGridProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 11/3/16.
 */
@Configuration
@EnableFeignClients(basePackageClasses = FacebookConfiguration.class)
public class FacebookConfiguration {

    private final FacebookUserInfoApi facebookUserInfoApi;
    private final FacebookUserGridAuthenticationApi facebookUserGridAuthenticationApi;
    private final UserGridApi userGridApi;
    private final AuthorityService authorityService;
    private final UserGridProperties userGridProperties;

    public FacebookConfiguration(final FacebookUserInfoApi facebookUserInfoApi, final FacebookUserGridAuthenticationApi facebookUserGridAuthenticationApi, final UserGridApi userGridApi, final AuthorityService authorityService, final UserGridProperties userGridProperties) {
        this.facebookUserInfoApi = facebookUserInfoApi;
        this.facebookUserGridAuthenticationApi = facebookUserGridAuthenticationApi;
        this.authorityService = authorityService;
        this.userGridApi = userGridApi;
        this.userGridProperties = userGridProperties;
    }

    @Bean
    public FacebookAuthenticationProvider facebookAuthenticationProvider() {
        return new FacebookAuthenticationProvider(authorityService, userGridApi, facebookUserInfoApi, facebookUserGridAuthenticationApi, userGridProperties);
    }
}
