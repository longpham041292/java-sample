package asia.cmg.f8.common.security.token;

import asia.cmg.f8.common.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created on 11/1/16.
 */
@Configuration
public class TokenConfiguration {

    @Bean
    @Qualifier("tokenStore")
    public JwtTokenStore jwtTokenStore() throws ApplicationException.InValid {
        return new JwtTokenStore(jwtTokenConverter());
    }

    @Bean
    public JwtTokenConverter jwtTokenConverter() throws ApplicationException.InValid {

        final JwtTokenConverter converter = new JwtTokenConverter();
        final Resource resource = new ClassPathResource("jwt.pub");

        if (!resource.exists()) {
            throw ApplicationException.inValid("jwt.pub is not found in classpath");
        }

        try {
            final String publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), Charset.defaultCharset());
            converter.setVerifierKey(publicKey);
        } catch (final IOException e) {
            throw ApplicationException.inValid("Error on reading public key.", e);
        }

        return converter;
    }
}
