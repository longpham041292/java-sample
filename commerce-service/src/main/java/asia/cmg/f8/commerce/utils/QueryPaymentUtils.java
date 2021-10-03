package asia.cmg.f8.commerce.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public final class QueryPaymentUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(QueryPaymentUtils.class);

    private QueryPaymentUtils(){}
    
    /**
     * This method is for creating a URL POST data string.
     *
     * @param queryString
     *            is the input String from POST data response
     * @return is a Hashmap of Post data response inputs
     */
    public static Map<String, String> createMapFromResponse(final String queryString) {
        final Map<String, String> map = new HashMap<>();
        final StringTokenizer strToken = new StringTokenizer(queryString, "&");
        while (strToken.hasMoreTokens()) {
            final String token = strToken.nextToken();
            final int idx = token.indexOf('=');
            if (idx > 0) {
                try {
                    final String key = token.substring(0, idx);
                    final String value = URLDecoder.decode(token.substring(idx + 1, token.length()),
                            PaymentUtils.UTF8);
                    map.put(key, value);
                } catch (final UnsupportedEncodingException exp) {
                    LOG.error("Encoding error {}", exp);
                }
            }
        }
        return map;
    }

    public static String createPostDataFromMap(final Map<String, Object> fields) {
        final StringBuilder buf = new StringBuilder();

        String ampersand = "";

        // append all fields in a data string
        for (final Iterator<String> iter = fields.keySet().iterator(); iter.hasNext();) {

            final String key = iter.next();
            final String value = (String) fields.get(key);

            if ((value != null) && (value.length() > 0)) {
                // append the parameters
                try {
                    buf.append(ampersand);
                    buf.append(URLEncoder.encode(key, PaymentUtils.UTF8));
                    buf.append('=');
                    buf.append(URLEncoder.encode(value, PaymentUtils.UTF8));
                } catch (final UnsupportedEncodingException exp) {
                    LOG.error("Error when encoding request data {}", exp);
                    return null;
                }
            }
            ampersand = "&";
        }

        // return string
        return buf.toString();
    }

}
