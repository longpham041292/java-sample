package asia.cmg.f8.commerce.onepay;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
* The class is copied from some external guideline from oneway.
*
* @author  sinh.vo
* @since   April 24, 2019 
*/
public class TokenCvvGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(TokenCvvGenerator.class);
	
    public static String generate(String tokenNumber, int expireMonth, int expireYear, String initCVV, int sequenceNumber, String payTime) {

        try {

            SecretKeySpec signKey = new SecretKeySpec(initCVV.getBytes(), _HMAC_SHA256_ALGORYTHM);

            Mac mac = Mac.getInstance(_HMAC_SHA256_ALGORYTHM);

            mac.init(signKey);

            String tokenData = tokenNumber + _NEW_LINE + expireMonth + _NEW_LINE + expireYear + _NEW_LINE + sequenceNumber + _NEW_LINE + payTime;

            byte[] hash = mac.doFinal(tokenData.getBytes());

            int offset = (hash[hash.length - 1] & 0xFF) % 28;

            long truncatedHash = 0;

            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            truncatedHash &= 0x7FFFFFFF;

            truncatedHash %= 10000;

            return ("000" + truncatedHash).replaceFirst("^\\d*(\\d{4})$", "$1");

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
        	LOG.error("Can not generate token for cvv  {}", ex);
            throw new IllegalArgumentException("Can not generate token for cvv ", ex);
        }
    }

    private static final String _HMAC_SHA256_ALGORYTHM = "HmacSHA256";

    private static final String _NEW_LINE = "\n";
}
