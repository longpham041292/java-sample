package asia.cmg.f8.gateway.security.apple;

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
@EnableFeignClients(basePackageClasses = AppleConfiguration.class)
public class AppleConfiguration {

    private final AuthorityService authorityService;

    public AppleConfiguration(final AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Bean
    public AppleAuthenticationProvider appleAuthenticationProvider() {
        return new AppleAuthenticationProvider(authorityService);
    }
}
