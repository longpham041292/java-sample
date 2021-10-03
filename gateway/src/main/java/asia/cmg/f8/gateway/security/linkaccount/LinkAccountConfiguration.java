package asia.cmg.f8.gateway.security.linkaccount;

import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import asia.cmg.f8.gateway.security.usergrid.UserGridProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 1/9/17.
 */
@Configuration
@EnableFeignClients(basePackageClasses = LinkAccountConfiguration.class)
@SuppressWarnings("PMD")
public class LinkAccountConfiguration {

    private final LinkedUserClient linkedUserClient;
    private final LinkedTokenClient linkedTokenClient;
    private final AccessTokenRepository accessTokenRepository;
    private final UserDetailService userDetailService;
    private final ObjectMapper objectMapper;
    private final AuthorityService authorityService;
    private final UserGridProperties userGridProperties;

    public LinkAccountConfiguration(final LinkedUserClient linkedUserClient, final LinkedTokenClient linkedTokenClient, final AccessTokenRepository accessTokenRepository, final UserDetailService userDetailService, final ObjectMapper objectMapper, final AuthorityService authorityService, final UserGridProperties userGridProperties) {
        this.linkedUserClient = linkedUserClient;
        this.linkedTokenClient = linkedTokenClient;
        this.accessTokenRepository = accessTokenRepository;
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
        this.authorityService = authorityService;
        this.userGridProperties = userGridProperties;
    }

    @Bean
    public LinkedAccountAuthenticationFilter linkedAccountAuthenticationFilter() {
        return new LinkedAccountAuthenticationFilter(linkedUserClient, linkedTokenClient, accessTokenRepository, userDetailService, objectMapper, authorityService, userGridProperties);
    }
}
