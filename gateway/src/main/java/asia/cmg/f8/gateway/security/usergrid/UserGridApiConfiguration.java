package asia.cmg.f8.gateway.security.usergrid;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import asia.cmg.f8.gateway.client.UserApiClient;
import asia.cmg.f8.gateway.security.token.JwtProperties;

/**
 * Created on 10/21/16.
 */
@EnableFeignClients(basePackageClasses = UserGridApiConfiguration.class)
@Configuration
@EnableConfigurationProperties(UserGridProperties.class)
@SuppressWarnings("PMD")
public class UserGridApiConfiguration {

    private final UserGridApi userGridApi;
    private final UserRoleApi roleApi;
    private final UserDetailApi userDetailApi;
    private final UserGridProperties userGridProperties;
    private final JwtProperties jwtProperties;
    private final UserGridApplicationValidation applicationAccessToken;

    public UserGridApiConfiguration(final UserGridApi userGridApi, final UserRoleApi roleApi, final UserDetailApi userDetailApi, final UserGridProperties userGridProperties, JwtProperties jwtProperties, final UserGridApplicationValidation applicationAccessToken) {
        this.userGridApi = userGridApi;
        this.roleApi = roleApi;
        this.userDetailApi = userDetailApi;
        this.userGridProperties = userGridProperties;
        this.jwtProperties = jwtProperties;
        this.applicationAccessToken = applicationAccessToken;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new UserGridAuthenticationProvider(authorityService(), userGridApi, userGridProperties, jwtProperties);
    }

    @Bean
    public UserGridFailFast userGridFailFast() {
        return new UserGridFailFast(applicationAccessToken, userGridProperties);
    }

    @Bean
    public UserGridAuthorityService authorityService() {
        return new UserGridAuthorityService(roleApi);
    }

    @Bean
    public UserGridUserDetailService userDetailService() {
        return new UserGridUserDetailService(userGridApi);
    }
    
    @Bean
	public PasswordEncoder encoder() {
	    return new BCryptPasswordEncoder();
	}
}
