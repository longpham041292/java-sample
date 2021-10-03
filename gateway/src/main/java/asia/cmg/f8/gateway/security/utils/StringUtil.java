package asia.cmg.f8.gateway.security.utils;

/**
 * Created on 11/3/16.
 */
public final class StringUtil {

    private StringUtil() {
        // empty
    }

    /**
     * Whether a string is empty.
     *
     * @param value the value
     * @return true if value is empty.
     */
    public static boolean isEmpty(final String value) {
        return value == null || value.length() == 0;
    }
}
