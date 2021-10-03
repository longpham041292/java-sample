package asia.cmg.f8.commerce.utils;

import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.config.PaymentProperties.PaymentModule;
import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.constants.PaymentType;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("squid:S00112")
public final class PaymentUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentUtils.class);

    public static final String UTF8 = StandardCharsets.UTF_8.displayName();
    private static final char[] HEX_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    private static final byte[] DECODE_HEX_ARRAY = new byte[103];

    static {
        int index = 0;
        for (final byte b : new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'}) {
            DECODE_HEX_ARRAY[b] = (byte) index++;
        }
        DECODE_HEX_ARRAY['a'] = DECODE_HEX_ARRAY['A'];
        DECODE_HEX_ARRAY['b'] = DECODE_HEX_ARRAY['B'];
        DECODE_HEX_ARRAY['c'] = DECODE_HEX_ARRAY['C'];
        DECODE_HEX_ARRAY['d'] = DECODE_HEX_ARRAY['D'];
        DECODE_HEX_ARRAY['e'] = DECODE_HEX_ARRAY['E'];
        DECODE_HEX_ARRAY['f'] = DECODE_HEX_ARRAY['F'];
    }

    public PaymentUtils() {
        // empty
    }


    public static String appendAndHashParams(final String gatewayUrl,
                                             final Map<String, Object> fields, final String secureSecret)
            throws UnsupportedEncodingException {
        final String secureHash = hashAllFields(fields, secureSecret);
        fields.put(PaymentConstant.VCP_SECURE_HASH, secureHash);
        final StringBuilder buf = new StringBuilder();
        buf.append(gatewayUrl).append('?');
        appendQueryFields(buf, fields);
        return buf.toString();
    }

    public static String hashAllFields(final Map<String, Object> fields, final String secureSecret) {
        final List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        final StringBuffer buf = new StringBuffer();
        final Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            final String fieldName = itr.next();
            final String fieldValue = (String) fields.get(fieldName);
            if (StringUtils.isNotEmpty(fieldValue) && fieldName.indexOf("vpc_") == 0) {
                buf.append(fieldName).append('=').append(fieldValue);
                if (itr.hasNext()) {
                    buf.append('&');
                }
            }
        }
        byte[] mac = null;
        try {
            final byte[] bHexa = decodeHexa(secureSecret.getBytes(UTF8));
            final SecretKey key = new SecretKeySpec(bHexa, "HMACSHA256");
            final Mac mObj = Mac.getInstance("HMACSHA256");
            mObj.init(key);
            mObj.update(buf.toString().getBytes(UTF8));
            mac = mObj.doFinal();
        } catch (final Exception excpt) {
            LOG.error("Error when decode data {}", excpt);
        }
        return hex(mac);
    }

    @SuppressWarnings("PMD")
    public static byte[] decodeHexa(final byte[] data) throws IllegalArgumentException {
        if (data == null) {
            return null;
        }
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid data length:" + data.length);
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte byte1;
        byte byte2;
        int index = 0;
        while (index < data.length) {
            byte1 = DECODE_HEX_ARRAY[data[index++]];
            byte2 = DECODE_HEX_ARRAY[data[index++]];
            out.write((byte1 << 4) | (byte2 & 0xff));
        }
        try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return out.toByteArray();
    }

    static String hex(final byte[] input) {
        final StringBuffer value = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            value.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
            value.append(HEX_TABLE[input[i] & 0xf]);
        }
        return value.toString();
    }

    static void appendQueryFields(final StringBuilder buf, final Map<String, Object> fields)
            throws UnsupportedEncodingException {
        final List<String> fieldNames = new ArrayList<>(fields.keySet());
        final Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            final String fieldName = itr.next();
            final String fieldValue = (String) fields.get(fieldName);

            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                buf.append(URLEncoder.encode(fieldName, UTF8));
                buf.append('=');
                buf.append(URLEncoder.encode(fieldValue, UTF8));
            }
            if (itr.hasNext()) {
                buf.append('&');
            }
        }
    }

    public static String nullToUnknown(final String input) {
        return StringUtils.isEmpty(input) ? PaymentConstant.NO_VALUE_RETURN : input;
    }

    public static String nullToUnknown(final String input, final Map<String, String> responseFields) {
        if (StringUtils.isEmpty(input) || responseFields.get(input) == null) {
            return PaymentConstant.NO_VALUE_RETURN;
        } else {
            return responseFields.get(input);
        }
    }

    public static Double getAmount(final PaymentProperties paymentProps,
                                   final PaymentModule paymentModule, final Double totalPrice) {
        return paymentProps.isTestMode() ? paymentModule.getTestAmount() : totalPrice;
    }

    /**
     * Multiply amount with 100.
     *
     * @param paymentProps get test amount in paymentProps
     * @param totalPrice   real totalPrice
     * @return amount
     */
    public static String getTransactionAmount(final PaymentProperties paymentProps,
                                              final PaymentModule paymentModule, final Double totalPrice) {
        final Double paymentAmount = paymentProps.isTestMode() ? paymentModule.getTestAmount()
                : totalPrice;
        return formatTransactionAmount(paymentAmount);
    }

    public static String formatTransactionAmount(final Double value) {
        return String.valueOf((long) Math.floor(value * 100));
    }

    public static Double getRealAmount(final String amount) {
        return Double.valueOf(amount) / 100;
    }

    public static String getDisplayPaymentLanguage(final String language) {
        return PaymentConstant.VN_LOCALE.equals(language) ? PaymentConstant.VN_GATEWAY_LOCALE
                : language;
    }

    public static boolean isPaymentSuccess(final OrderEntity order) {
        return PaymentStatus.PAID.equals(order.getPaymentStatus())
                && OrderStatus.COMPLETED.equals(order.getOrderStatus());
    }

    public static boolean isDomesticPayment(final PaymentType paymentType) {
        return PaymentType.DOMESTIC.equals(paymentType);
    }

    /**
     * Order is in the END state. Can't modify.
     *
     * @param order order
     * @return is order finish
     */
    public static boolean isOrderFinish(final OrderEntity order) {
        return OrderStatus.CANCELLED.equals(order.getOrderStatus())
                || OrderStatus.COMPLETED.equals(order.getOrderStatus());
    }

}
