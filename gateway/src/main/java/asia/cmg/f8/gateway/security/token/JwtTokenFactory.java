package asia.cmg.f8.gateway.security.token;

import asia.cmg.f8.gateway.security.auth.TokenAuthentication;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.jwt.Jwt;

/**
 * Created on 10/20/16.
 */
public interface JwtTokenFactory {

    String encode(TokenAuthentication authentication) throws IOException;
    
    JwtTokenDTO decode(String token) throws Exception;

	String encode(String userUuid, long expireAt) throws IOException;
}
