package asia.cmg.f8.common.util;

import java.util.Optional;

/**
 * Created on 1/5/17.
 */
public final class TokenUtils {

    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private TokenUtils() {
        // empty
    }

    /**
     * A convenience method to convert a raw token to Bearer token
     *
     * @param token the raw token
     * @return bearer token
     */
    public static String toBearer(final String token) {
        return BEARER_TOKEN_PREFIX + token;
    }

    /**
     * A convenience method to extract raw token from given Bearer token.
     *
     * @param token the bearer token
     * @return the raw token.
     */
    public static Optional<String> fromBearer(final String token) {
        if (token != null && token.startsWith(BEARER_TOKEN_PREFIX)) {
            return Optional.of(token.substring(BEARER_TOKEN_PREFIX.length()));
        }
        return Optional.empty();
    }
}
