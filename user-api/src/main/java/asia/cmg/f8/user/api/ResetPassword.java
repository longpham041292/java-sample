package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.email.EmailSender;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.dto.ResetUserPassword;
import asia.cmg.f8.user.entity.AccountTokenEntity;
import asia.cmg.f8.user.entity.BasicUserEntity;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.repository.BasicUserRepository;
import asia.cmg.f8.user.service.AccountTokenService;
import asia.cmg.f8.user.service.JwtService;
import asia.cmg.f8.user.service.UserService;
import asia.cmg.f8.user.util.ResetPasswordEmail;

@RestController
public class ResetPassword {

	private static final String SUCCESS = "success";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final Logger LOG = LoggerFactory.getLogger(ResetPassword.class);
	private static final String SENDING_EMAIL_FROM = "Leep.app <lisa@leep.app>";
	private static final String FORGET_PASSWORD_EN = "forget_password_en";
	private static final String FORGET_PASSWORD_VI = "forget_password_vi";
	private static final String NAME = "name";
	private static final String URL = "url";
	private static final String LOGO_URL = "logoUrl";
	private static final String APP_STORE = "appStore";
	private static final String GOOGLE_PLAY = "googlePlay";
	private static final String DECOR = "decor";
	private static final String DIVIDER = "divider";
	private static final String CONTACT = "contact";

	@Autowired
	private AccountTokenService accountTokenService;

	@Autowired
	private BasicUserRepository userRepo;

	@Autowired
	private UserClient userClient;

	@Value("${jwt.access_expiresIn}")
	private int accessExpiresIn;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserService userService;

	@Autowired
	private SpringTemplateEngine templateEngine;
	
	@Autowired
	private ResetPasswordEmail resetPasswordEmail;

	@RequestMapping(value = "/users/v1/reset-password", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> resetPassword(@RequestParam(value = "email", required = true) final String email,
			@RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String lang) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		// check email exist in DB
		Optional<BasicUserEntity> userOptional = userRepo.findByEmail(email);
		if (!userOptional.isPresent()) {
			LOG.warn("[Reset password]: Email {} is not exist in system", email);
			return new ResponseEntity<>(
					ErrorCode.USER_NOT_EXIST.withError("EMAIL_NOT_EXIST", "User is not exist in DB"),
					HttpStatus.BAD_REQUEST);
		}
		// check email exist in usergrid
		UserGridResponse<UserEntity> response = userClient.getUserByQuery("where email = '" + email + "'");
		if (response == null || response.getEntities() == null || response.getEntities().isEmpty()) {
			LOG.error("[Reset password]: Email {} is not exist in usergrid", email);
			return new ResponseEntity<>(
					ErrorCode.USER_NOT_EXIST.withError("EMAIL_NOT_EXIST", "User is not exist in Usergrid"),
					HttpStatus.BAD_REQUEST);
		}

		// get dto AccountTokenEntity
		BasicUserEntity user = userOptional.get();
		AccountTokenEntity token = accountTokenService.findByUUID(user.getUuid());
		if (token == null) {
			token = new AccountTokenEntity();
			token.setUuid(user.getUuid());
		}

		// Generate new jwt key
		long resetPasswordExpired = System.currentTimeMillis() + accessExpiresIn;
		String resetPasswordToken = jwtService.generateToken(user.getUuid(), ACCESS_TOKEN);
		token.setResetPasswordToken(resetPasswordToken);
		token.setResetPasswordExpired(resetPasswordExpired);
		accountTokenService.saveToken(token);
		//send email
		Locale locale = Locale.forLanguageTag(lang.toLowerCase());
		Context context = new Context(locale);
		String name = lang.equalsIgnoreCase("vi") ? "Chào " + user.getFullName() : "Hi " + user.getFullName();
		context.setVariable(NAME, name);
		context.setVariable(URL, resetPasswordEmail.getUrl() + resetPasswordToken + "&lang=" + lang);
		context.setVariable(LOGO_URL, resetPasswordEmail.getLogoUrl());
		context.setVariable(APP_STORE, resetPasswordEmail.getAppStore());
		context.setVariable(GOOGLE_PLAY, resetPasswordEmail.getGooglePlay());
		context.setVariable(DECOR, resetPasswordEmail.getDecor());
		context.setVariable(DIVIDER, resetPasswordEmail.getDivider());
		context.setVariable(CONTACT, resetPasswordEmail.getContact());
		String emailTemplate = lang.equalsIgnoreCase("vi") ? templateEngine.process(FORGET_PASSWORD_VI, context)
				: templateEngine.process(FORGET_PASSWORD_EN, context);
		EmailSender emailSend = new EmailSender();
		Boolean result = emailSend.sendEmail(SENDING_EMAIL_FROM, email, "Reset password", emailTemplate);
		if (!result) {
			return new ResponseEntity<>(
					new ErrorCode(4001, "FAIL_SEND_EMAIL", "Failed").withError("FAIL_SEND_EMAIL", "FAIL_SEND_EMAIL"),
					HttpStatus.BAD_REQUEST);
		}

		apiResponse.setData(Boolean.TRUE);
		return new ResponseEntity<>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/users/v1/reset-password", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateResetPassword(@RequestBody final Map<String, String> dto) {
		// check token valid
		String token = dto.get("token");
		String password = dto.get("password");
		AccountTokenEntity tokenDto = accountTokenService.findByResetPasswordToken(token);

		if (tokenDto == null) {
			LOG.warn("[Reset password]: Token reset password invalid {}", token);
			return new ResponseEntity<>(
					new ErrorCode(4011, "TOKEN_INVALID", "Failed").withError("TOKEN_INVALID", "Token invalid"),
					HttpStatus.BAD_REQUEST);
		}
		if (tokenDto.getResetPasswordExpired() <= System.currentTimeMillis()) {
			LOG.warn("[Reset password]: Token reset password expire {}", token);
			return new ResponseEntity<>(
					new ErrorCode(4012, "TOKEN_EXPIRE", "Failed").withError("TOKEN_EXPIRE", "Token expire"),
					HttpStatus.BAD_REQUEST);
		}
		ResetUserPassword passwordBody = new ResetUserPassword();
		passwordBody.setNewPassword(password);
		if (!userService.resetUserPassword(tokenDto.getUuid(), passwordBody)) {
			LOG.warn("Can not change password");
			return new ResponseEntity<>(
					ErrorCode.REQUEST_INVALID.withError("INVALID_CHANGE_PASSWORD", "Can not change password"),
					HttpStatus.BAD_REQUEST);
		}
		tokenDto.setResetPasswordToken(null);
		tokenDto.setResetPasswordExpired(null);
		accountTokenService.saveToken(tokenDto);
		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
	}

}
