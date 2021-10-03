package asia.cmg.f8.gateway;

/**
 * Created on 10/20/16.
 */
public interface SecurityUtil {

    String AUTHORIZATION_HEADER = "authorization";
    String BEARER_PREFIX = "Bearer ";


    /**
     * Convert a token value to Bearer token.
     *
     * @param token the token
     * @return Bearer token string
     */
    static String toBearer(final String token) {
        return BEARER_PREFIX + token;
    }

    static boolean isBearerToken(final String token) {
        return token != null && token.startsWith(BEARER_PREFIX);
    }

    static String fromBearer(final String token) {
        if (isBearerToken(token)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
