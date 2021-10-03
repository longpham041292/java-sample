package asia.cmg.f8.common.security.config;

import asia.cmg.f8.common.security.internal.DefaultRolesPrefixPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 12/1/16.
 */
@Configuration
public class RoleConfiguration {

    @Bean
    public DefaultRolesPrefixPostProcessor defaultRolesPrefixPostProcessor() {
        return new DefaultRolesPrefixPostProcessor();
    }
}
