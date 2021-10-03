package asia.cmg.f8.common.sms;

import static asia.cmg.f8.common.sms.SmsConstants.BRAND_NAME;
import static asia.cmg.f8.common.sms.SmsConstants.CMC_URL;
import static asia.cmg.f8.common.sms.SmsConstants.PASS;
import static asia.cmg.f8.common.sms.SmsConstants.USER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.sms.dto.CMCSendSMSResponse;
import asia.cmg.f8.common.sms.dto.CmcSmsRequest;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

public class SmsSender {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsSender.class);

	public ResponseEntity<?> sendSmsMessage(String phone, String message) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		CmcSmsRequest request = new CmcSmsRequest();
		request.setBrandname(BRAND_NAME);
		request.setUser(USER);
		request.setPass(PASS);
		request.setMessage(message);
		request.setPhonenumber(phone);
		CMCSendSMSResponse response = new RestTemplate().postForObject(CMC_URL, request, CMCSendSMSResponse.class);
		if (response.getData() == null || response.getData().getStatus() != 1) {
			LOGGER.error("Can not send message to Phone {} . Message: {}", phone, response.getDescription());
			return new ResponseEntity<>(new ErrorCode(4013, "FAIL_SEND_SMS_TO_PHONE", "FAIL_SEND_SMS_TO_PHONE")
					.withError("FAIL_SEND_SMS_TO_PHONE", response.getDescription()), HttpStatus.BAD_REQUEST);
		}
		apiResponse.setData(true);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

}
