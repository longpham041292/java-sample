package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.service.OtpService;
import asia.cmg.f8.user.service.UserService;

@RestController
public class OtpAPI {
	private static final Logger LOG = LoggerFactory.getLogger(OtpAPI.class);
	@Autowired
	private OtpService otpService;
	
	@Autowired 
	private UserService userService;

	@RequestMapping(value = "/otp/verify", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> verifyOtpSMS(final Account account, @RequestParam(value = "code") Integer code) {

		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		
		apiResponse.setStatus(ErrorCode.SUCCESS);
		apiResponse.setData(false);
		String phone = userService.getPhoneNumber(account.uuid());
		boolean isValidCode = otpService.isValidCode(account.uuid() + phone, code);
		if (isValidCode) {
			apiResponse.setData(userService.updateVerifyPhone(account.uuid()));
		} else {
			LOG.info("verifyOtpSMS: OTP code {} invalid, Uuid {}, Phone {}", code, account.uuid(), phone);
		}
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}
}
