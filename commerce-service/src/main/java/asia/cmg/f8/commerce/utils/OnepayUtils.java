package asia.cmg.f8.commerce.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.dto.onepay.InstrumentDto;
import asia.cmg.f8.commerce.dto.onepay.TransactionDto;
import asia.cmg.f8.commerce.dto.onepay.UserAddressDto;
import asia.cmg.f8.commerce.dto.onepay.UserDto;
import asia.cmg.f8.commerce.dto.onepay.PaymentOrderRequestDto;
import asia.cmg.f8.commerce.dto.onepay.PaymentRequestDto;
import asia.cmg.f8.commerce.dto.onepay.PaymentTokenRequestDto;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.OnepayInstrumentEntity;
import asia.cmg.f8.commerce.entity.OnepayUserEntity;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.onepay.Authorization;
import asia.cmg.f8.commerce.onepay.TokenCvvGenerator;

public final class OnepayUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(OnepayUtils.class);
	
	public static final String TSP_USERS_URL = "/tsp/api/v2/users";
	public static final String TSP_INSTRUMENT_URL = "/tsp/api/v2/instruments";
	public static final String TSP_PAYMENT_URL = "/tsp/api/v2/payments";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	public static String getValFromMap(final Map<String, Object> responseFields, final String fieldName) {
		return responseFields.getOrDefault(fieldName, StringUtils.EMPTY).toString();
	}

	public static String getOnepayDateFormat(final Date date) {
		return DATE_FORMAT.format(date);
	}

	public static PaymentRequestDto buildPaymentRequest(OnepayInstrumentEntity instrument,
			final OnepayUserEntity onepayUser, final OrderEntity order, final String transactionRef,
			final Double amount, final String returnUrl) {
		final PaymentRequestDto paymentRequest = new PaymentRequestDto();
		paymentRequest.setMerchantId(instrument.getMerchantId());
		paymentRequest.setMerchantTxnRef(transactionRef);
		paymentRequest.setUserId(onepayUser.getOnepayUserId());
		paymentRequest.setAmount(amount);
		paymentRequest.setCurrency(order.getCurrency());
		paymentRequest.setOrder(new PaymentOrderRequestDto(transactionRef, order.getCode()));

		final PaymentTokenRequestDto tokenDto = new PaymentTokenRequestDto();
		tokenDto.setNumber(instrument.getTokenNumber());
		tokenDto.setExpireMonth(instrument.getTokenExpireMonth());
		tokenDto.setExpireYear(instrument.getTokenExpireYear());

		final String payTimeasString = OnepayUtils.getOnepayDateFormat(new Date());
		final int sequenceNumber = getSequenceNumber();
		final String tcvv = new TokenCvvGenerator().generate(instrument.getTokenNumber(),
				instrument.getTokenExpireMonth(), instrument.getTokenExpireYear(), instrument.getTokenCvv(),
				sequenceNumber, payTimeasString);
		int cvv = Integer.parseInt(tcvv);
		tokenDto.setCvv(cvv);
		tokenDto.setPayTime(payTimeasString);
		tokenDto.setSequenceNumber(sequenceNumber);

		paymentRequest.setToken(tokenDto);
		paymentRequest.setReturnUrl(returnUrl);

		return paymentRequest;
	}

	public static UserDto buildUserRequest(final PaymentProperties paymentProps, final BasicUserEntity user,
			final Map<String, Object> paymentResFields) {
		final UserAddressDto userAddress = new UserAddressDto();
		userAddress.setLine1(OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VPC_AVS_STREET1));
		userAddress.setState(OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VPC_STATE));
		userAddress.setCity(OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VPC_CITY));
		userAddress.setCountryCode(OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VPC_COUNTRY_CODE));

		final UserDto onepayUser = new UserDto();
		onepayUser.setGroupId(paymentProps.getGroupId());
		onepayUser.setRefId(user.getId().toString());
		onepayUser.setFirstName(user.getFullName());
		onepayUser.setLastName(user.getFullName());
		onepayUser.setMobile(user.getPhone());
		onepayUser.setEmail(user.getEmail());
		onepayUser.setAddress(userAddress);

		return onepayUser;
	}

	public static InstrumentDto buildInstrumentRequest(final OnepayUserEntity onepayUser,
			final TransactionDto transactionReq) {
		final InstrumentDto instrumentRequest = new InstrumentDto();
		instrumentRequest.setUserId(onepayUser.getOnepayUserId());
		instrumentRequest.setTransaction(transactionReq);
		instrumentRequest.setCreateToken(true);
		return instrumentRequest;
	}

	public static ResponseEntity<String> sendRequest(HttpMethod method, String uri, String body,
			final PaymentProperties paymentProperties) throws Exception {

		Map<String, String> queryParams = new LinkedHashMap<String, String>();
		Map<String, String> signedHeaders = new LinkedHashMap<String, String>();
		signedHeaders.put("Content-Type", "application/json");
		signedHeaders.put("Accept", "application/json");
		signedHeaders.put("Accept-Language", "vi");
		signedHeaders.put(Authorization.X_OP_DATE_HEADER, OnepayUtils.getOnepayDateFormat(new Date()));
		signedHeaders.put(Authorization.X_OP_EXPIRES_HEADER, "900");
		byte[] bd = null == body ? "".getBytes("UTF-8") : body.getBytes("UTF-8");
		Authorization auth = new Authorization(paymentProperties.getOnepayAccessKeyId(),
				paymentProperties.getOnepaySecretAccessKey(), paymentProperties.getOnepayRegion(),
				paymentProperties.getOnepayService(), method.toString(), uri, queryParams, signedHeaders, bd);

		String queryString = "?";
		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			queryString += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		}
		queryString = queryString.substring(0, queryString.length() - 1);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		headers.put("Accept-Language", "vi");
		headers.put("X-OP-Authorization", auth.toString());
		headers.put("X-OP-Date", auth.getTimeStampString());
		headers.put("X-OP-Expires", "900");
		final String fullUrl = paymentProperties.getOnepayBaseUrl() + uri + queryString;
		LOG.info("Send to onepay url: {} with header {}", fullUrl, headers);
		return httpRequest(fullUrl, method, headers, bd);
	}

	private static ResponseEntity<String> httpRequest(String url, HttpMethod method, Map<String, String> headers,
			byte[] body) throws Exception {

		URL obj = new URL(url);
		
		HttpsURLConnection conn = null;
		ByteArrayOutputStream bout = null;
		DataOutputStream wr = null;
		String resultContent = StringUtils.EMPTY;
		int responseCode = 0;
		try {
			
			conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod(method.toString());

			if (headers != null) {
				for (String key : headers.keySet()) {
					conn.setRequestProperty(key, headers.get(key));
				}
			}
			if (method.matches("^(POST|PUT)$")) {
				conn.setRequestProperty("Content-Length", String.valueOf(body != null ? body.length : "0"));
			}
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);
			if (body != null && body.length > 0) {
				conn.setDoOutput(true);
				wr = new DataOutputStream(conn.getOutputStream());
				wr.write(body);
				wr.flush();
			} else {
				conn.setDoOutput(false);
			}
			
			responseCode = conn.getResponseCode();

			byte[] content = null;
			if (conn.getDoInput()) {
				InputStream is;
				if (responseCode >= 400) {
					is = conn.getErrorStream();
				} else {
					is = conn.getInputStream();
				}
				if (is != null) {
					bout = new ByteArrayOutputStream();
					byte[] buf = new byte[1000];
					int length;
					while ((length = is.read(buf)) != -1) {
						bout.write(buf, 0, length);
					}
					bout.flush();
					content = bout.toByteArray();
				}
			}
			resultContent = (content != null ? new String(content, "UTF-8") : null);
		} catch (final Exception exp) {
			LOG.error("Error to send http request {}", exp);
		} finally {
			IOUtils.closeQuietly(wr);
			IOUtils.closeQuietly(bout);
			IOUtils.closeQuietly(wr);
			conn.disconnect();
		}
		
		return new ResponseEntity<String>(resultContent, HttpStatus.valueOf(responseCode));
	}

	private static int getSequenceNumber() {
		return new Random().nextInt(Integer.MAX_VALUE);
	}
}
