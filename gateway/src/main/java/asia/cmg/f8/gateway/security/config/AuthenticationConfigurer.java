package asia.cmg.f8.gateway.security.config;

import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import asia.cmg.f8.gateway.security.api.AuthorityService;
import asia.cmg.f8.gateway.security.api.UserDetailService;
import asia.cmg.f8.gateway.security.auth.AccessTokenLogoutFilter;
import asia.cmg.f8.gateway.security.auth.AccessTokenLogoutHandler;
import asia.cmg.f8.gateway.security.auth.AccessTokenLogoutSuccessHandler;
import asia.cmg.f8.gateway.security.auth.ActuatorFilter;
import asia.cmg.f8.gateway.security.auth.DefaultAuthenticationFailureHandler;
import asia.cmg.f8.gateway.security.auth.DefaultAuthenticationProcessingFilter;
import asia.cmg.f8.gateway.security.auth.DefaultAuthenticationSuccessHandler;
import asia.cmg.f8.gateway.security.auth.DefaultSecurityContextRepository;
import asia.cmg.f8.gateway.security.auth.ResetPwdLogoutFilter;
import asia.cmg.f8.gateway.security.auth.SecuredRequestWrapperFilter;
import asia.cmg.f8.gateway.security.linkaccount.LinkedAccountAuthenticationFilter;
import asia.cmg.f8.gateway.security.token.JwtTokenFactory;
import asia.cmg.f8.gateway.security.usergrid.UserGridLogoutApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Map;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

/**
 * Created on 10/19/16.
 */
@Configuration
@EnableWebSecurity
@Order(value = 100)
@SuppressWarnings("PMD")
public class AuthenticationConfigurer extends WebSecurityConfigurerAdapter {

    private final ApplicationContext context;
    private final JwtTokenFactory tokenFactory;
    private final UserGridLogoutApi userGridLogoutApi;
    private final UserDetailService userDetailService;
    private final AuthorityService authorityService;
    private final AccessTokenRepository accessTokenRepository;
    private final LinkedAccountAuthenticationFilter linkedAccountAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public AuthenticationConfigurer(final ApplicationContext context, final JwtTokenFactory tokenFactory, final UserGridLogoutApi userGridLogoutApi, final UserDetailService userDetailService, final AuthorityService authorityService, final AccessTokenRepository accessTokenRepository, final LinkedAccountAuthenticationFilter linkedAccountAuthenticationFilter, final ObjectMapper objectMapper) {
        this.context = context;
        this.tokenFactory = tokenFactory;
        this.userGridLogoutApi = userGridLogoutApi;
        this.userDetailService = userDetailService;
        this.authorityService = authorityService;
        this.accessTokenRepository = accessTokenRepository;
        this.linkedAccountAuthenticationFilter = linkedAccountAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .csrf().disable() // disable for JWT usage
                .httpBasic().disable()
                .formLogin().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .addFilter(accessTokenLogoutFilter())
                .addFilterBefore(resetPwdLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(securityContextPersistenceFilter(), SecurityContextPersistenceFilter.class)
                .addFilterBefore(linkedAccountAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(userGridAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securedRequestWrapperFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(actuatorFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(POST,
                        "/token/**",
                        "/users/signup/**",
                        "/users/*/signup/**",
                        "/users/signin/**",
                        "/users/resetpw/**",
                        "/users/reactivate/**",
                        "/users/*/resetpassword/**",
                        "/users/*/reset-password/**",
                        "/files/**",
                        "/payment/return/**",
                        "/tickets",
                        "/commerce/public/**"
                ).permitAll()
                .antMatchers(PUT,
                        "/notifications/devices/**"
                ).permitAll()
                .antMatchers(GET,
                        "/users/exist/**",
                        "/users/activated/**",
                        "/lists/**",
                        "/configs/**",
                        "/pubClubs/**",
                        "/users/*/confirm",
						"/register/*/confirm",
                        "/ejabberd/**",
                        "/payment/return/**",
                        "/posts/**",
                        "/social/posts/**",
                        "/apple-app-site-association/**",
                        "/health",
                        "/clubs/**",
                        "/profile/mobile/v1/question_groups",
                        "/profile/public/**",
                        "/commerce/public/**",
                        "/users/*/reset-password/**"
                ).permitAll()
                .antMatchers(OPTIONS).permitAll()
                .anyRequest().authenticated();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // @formatter:on
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public ResetPwdLogoutFilter resetPwdLogoutFilter() {
        return new ResetPwdLogoutFilter("/users/*/resetpassword/**");
    }
    
    @Bean
    public ActuatorFilter actuatorFilter() {
        return new ActuatorFilter("/*/actuator/**");
    }

    @Bean
    public AccessTokenLogoutFilter accessTokenLogoutFilter() {
        final AccessTokenLogoutHandler logoutHandler = new AccessTokenLogoutHandler();
        final SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        final AccessTokenLogoutSuccessHandler accessTokenLogoutSuccessHandler = new AccessTokenLogoutSuccessHandler();
        return new AccessTokenLogoutFilter(accessTokenLogoutSuccessHandler, userDetailService, userGridLogoutApi, logoutHandler, securityContextLogoutHandler);
    }

    @Bean
    @SuppressWarnings("PMD")
    public DefaultAuthenticationProcessingFilter userGridAuthenticationProcessingFilter() throws Exception {

        final AuthenticationManager authenticationManager = authenticationManager();

        final DefaultAuthenticationProcessingFilter authenticationFilter = new DefaultAuthenticationProcessingFilter(objectMapper);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return authenticationFilter;
    }

    @Override
    @SuppressWarnings("PMD")
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        final Map<String, AuthenticationProvider> providers = context.getBeansOfType(AuthenticationProvider.class);
        if (providers != null) {
            providers.values().forEach(auth::authenticationProvider);
        }
    }

    private DefaultAuthenticationFailureHandler authenticationFailureHandler() {
        return new DefaultAuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new DefaultAuthenticationSuccessHandler(objectMapper);
    }

    @Bean
    protected SecurityContextPersistenceFilter securityContextPersistenceFilter() {
        return new SecurityContextPersistenceFilter(securityContextRepository());
    }

    @Bean
    public DefaultSecurityContextRepository securityContextRepository() {
        return new DefaultSecurityContextRepository(accessTokenRepository, userDetailService, authorityService);
    }

    @Bean
    public SecuredRequestWrapperFilter securedRequestWrapperFilter() {
        return new SecuredRequestWrapperFilter(tokenFactory);
    }
}
