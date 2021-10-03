package asia.cmg.f8.gateway.security.token;

import asia.cmg.f8.gateway.config.GatewayProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

/**
 * Created on 10/20/16.
 */
@Configuration
@EnableConfigurationProperties(value = {JwtProperties.class, GatewayProperties.class})
public class JwtConfiguration {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtProperties jwtProperties;
    private final GatewayProperties gatewayProperties;

    public JwtConfiguration(final JwtProperties jwtProperties, final GatewayProperties gatewayProperties) {
        this.jwtProperties = jwtProperties;
        this.gatewayProperties = gatewayProperties;
    }

    @Bean
    public JwtTokenFactory jwtTokenFactory() {

        // TODO move to configuration
        final String keyStorePass = jwtProperties.getKeyPass();
        final String alias = jwtProperties.getAlias();

        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("f8_dev.jks"), keyStorePass.toCharArray());
        return new JwtTokenFactoryImpl(keyStoreKeyFactory.getKeyPair(alias),
                objectMapper, jwtProperties, gatewayProperties);
    }
}
