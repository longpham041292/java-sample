package asia.cmg.f8.user.service;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.sms.SmsSender;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.config.OtpProperties;
import asia.cmg.f8.user.entity.BasicUserEntity;
import asia.cmg.f8.user.repository.BasicUserRepository;

@Service
public class OtpService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OtpService.class);
	@Autowired
	private BasicUserRepository basicUserRepo;

	@Autowired
	private OtpProperties otpProperties;

	public Integer getOTPCode(String uuid) {
		return this.createGoogleAuthenticator().getTotpPassword(uuid + otpProperties.getSaltSecret());
	}

	public boolean isValidCode(String uuid, Integer code) {
		return this.createGoogleAuthenticator().authorize(uuid + otpProperties.getSaltSecret(), code);
	}

	private GoogleAuthenticator createGoogleAuthenticator() {
		GoogleAuthenticatorConfigBuilder cb = new GoogleAuthenticatorConfigBuilder();
		cb.setCodeDigits(6).setNumberOfScratchCodes(6)
				.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(otpProperties.getTimeStepSizeInSecond()))
				.setWindowSize(otpProperties.getWindowSize());
		GoogleAuthenticator gAuth = new GoogleAuthenticator(cb.build());
		return gAuth;
	}

	public ResponseEntity<?> sendSmsVerificationPhone(String uuid, String code, String lang) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		Optional<BasicUserEntity> userOptional = basicUserRepo.findByUuid(uuid);
		if (!userOptional.isPresent()) {
			LOGGER.info("User {} is not existed", uuid);
			return new ResponseEntity<>(ErrorCode.USER_NOT_EXIST.withError("USER_NOT_EXIST", "USER_NOT_EXIST"),
					HttpStatus.BAD_REQUEST);
		}

		BasicUserEntity user = userOptional.get();

		String phone = user.getPhone().replace("+", ""); // remove + char if phone is +84...

		if (phone.isEmpty() || phone.length() < 8) {
			LOGGER.error("User {} with {} phone number is not existed", uuid, phone);
			return new ResponseEntity<>(new ErrorCode(2004, "PHONE_NOT_EXISTED", "PHONE_NOT_EXISTED")
					.withError("PHONE_NOT_EXISTED", "PHONE_NOT_EXISTED"), HttpStatus.BAD_REQUEST);
		}

		// Sanitize Phone
		if (phone.charAt(0) == Character.valueOf('0')) { // remove 0 at begin
			phone = phone.replaceFirst("0", "");
		}

		String messageLanguage = (lang.equalsIgnoreCase("VI")) ? otpProperties.getMessageVi()
				: otpProperties.getMessageEn();
		byte[] decodedBytes = Base64.getDecoder().decode(messageLanguage);
		String messageTemplate = new String(decodedBytes);
		String message = messageTemplate.replace("%%code%%", code);
		SmsSender sms = new SmsSender();
		return sms.sendSmsMessage(phone, message);
	}
}
