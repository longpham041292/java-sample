package asia.cmg.f8.session.config;

import asia.cmg.f8.common.security.annotation.EnableClientSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Created by tri.bui on 10/25/16.
 */
@Configuration
@EnableClientSecurity
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    @SuppressWarnings("PMD")
    public void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/calendar/**").authenticated()
                .anyRequest().permitAll();
    }
}
