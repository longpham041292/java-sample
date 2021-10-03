package asia.cmg.f8.commerce.internal;

/**
 * Created on 12/30/16.
 */
public final class OrderCodeUtil {

    private static final int NUM_OF_DIGITS = 6;

    private OrderCodeUtil() {
        // empty
    }

    public static String build(final int value) {

        if (value < 0) {
            throw new IllegalStateException("Do not support generate order code with negative value \"" + value + "\"");
        }

        final String valueString = String.valueOf(value);

        final int numOfZeroDigits = NUM_OF_DIGITS - valueString.length();

        if (numOfZeroDigits <= 0) {
            // we don't expect this case but if it happen, we throw exception...
            throw new IllegalStateException("Unique order code generator ran out of its quota. Support maximum " + NUM_OF_DIGITS + " digits.");
        }

        final StringBuilder string = new StringBuilder();
        for (int i = 0; i < numOfZeroDigits; i++) {
            string.append('0');
        }

        string.append(value);
        return string.toString();
    }
}
