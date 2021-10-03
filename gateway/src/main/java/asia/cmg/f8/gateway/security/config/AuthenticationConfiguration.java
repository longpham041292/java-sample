package asia.cmg.f8.gateway.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;

import asia.cmg.f8.gateway.security.api.AccessTokenRepository;
import asia.cmg.f8.gateway.security.auth.InMemoryAccessTokenRepository;

/**
 * Created on 1/9/17.
 */
@Configuration
public class AuthenticationConfiguration {

    private final HazelcastInstance hazelcastInstance;

    public AuthenticationConfiguration(final HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Bean
    public AccessTokenRepository accessTokenRepository() {
        return new InMemoryAccessTokenRepository(hazelcastInstance);
    }
}
