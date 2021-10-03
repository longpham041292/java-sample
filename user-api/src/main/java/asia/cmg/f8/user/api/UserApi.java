package asia.cmg.f8.user.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.order.ImportUserResult;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.user.client.SessionClient;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.dto.ChangeExtendUserTypeRequest;
import asia.cmg.f8.user.dto.PhoneNumberDTO;
import asia.cmg.f8.user.entity.AdminActivateRequest;
import asia.cmg.f8.user.exception.InvalidEmailException;
import asia.cmg.f8.user.service.OtpService;
import asia.cmg.f8.user.service.UserManagementService;
import asia.cmg.f8.user.service.UserService;

/**
 * Created on 10/11/16.
 */
@RestController
public class UserApi {

	private static final Logger LOG = LoggerFactory.getLogger(UserApi.class);
	private static final String EXIST = "exist";
	private static final String VALID = "valid";
	private static final String ACTIVATED = "activated";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String SUCCESS = "success";
	private static final int USER_CODE_VALID = 0;

	private static final ErrorCode USER_NOT_EXIST = ErrorCode.REQUEST_INVALID.withError("USER_NOT_EXIST",
			"User is not EXIST");
	private static final ErrorCode USER_NOT_ACTIVATED = ErrorCode.REQUEST_INVALID.withError("USER_NOT_ACTIVATED",
			"User is not activated");
	private static final ErrorCode USER_IS_ACTIVATED = ErrorCode.REQUEST_INVALID.withError("USER_IS_ACTIVATED",
			"User is activated");

	private final UserClient userClient;

	private final UserService userService;
	private final UserManagementService userManagementService;
	private final SessionClient sessionClient;

	@Autowired
	private OtpService otpService;

	@Inject
	public UserApi(final UserClient userClient, final UserService userService,
			final UserManagementService userManagementService, final SessionClient sessionClient) {
		this.userClient = userClient;
		this.userService = userService;
		this.userManagementService = userManagementService;
		this.sessionClient = sessionClient;
	}

	@RequestMapping(value = "/exist", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> checkUsernameExist(@RequestParam final String username,
			final HttpServletRequest request) {

		boolean exist;
		try {
			exist = userManagementService.isUserNameExisted(username);
		} catch (final UserGridException uge) {
			exist = false;
			LOG.warn("Error on checking username existed", uge);
		}
		return Collections.singletonMap(EXIST, exist);
	}

	@RequestMapping(value = "/exist/email", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> checkEmailExist(@RequestParam final String email, final HttpServletRequest request) {
		boolean exist;
		try {
			exist = userManagementService.isEmailExisted(email);
		} catch (final UserGridException uge) {
			exist = false;
			LOG.warn("Error on get user information.", uge);
		}
		return Collections.singletonMap(EXIST, exist);
	}

	@RequestMapping(value = "/valid/usercode", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkUserCodeValid(@RequestParam final String usercode,
			@RequestParam final String usertype, final HttpServletRequest request) {
		boolean valid = true;
		LOG.info("--- >>> --- >>> {} -- {} ", usercode, usertype);

		final ImportUserResult validUserCode = sessionClient.isValidUserCodeInMigrationDB(usercode, usertype);

		LOG.info("--- <<<<  {} -- {} ", USER_CODE_VALID, validUserCode.getErrorCode());
		if (USER_CODE_VALID != validUserCode.getErrorCode()) {
			return new ResponseEntity<>(Collections.singletonMap(VALID, false), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(validUserCode, HttpStatus.OK);

	}

	@RequestMapping(value = "/resetpw", method = POST)
	public ResponseEntity resetPw(final @RequestBody Map<String, Object> request) {
		try {
			final String email = (String) request.get("email");
			if (!userManagementService.isEmailExisted(email)) {
				return new ResponseEntity<>(USER_NOT_EXIST, HttpStatus.NOT_FOUND);
			}
			final boolean activated = userManagementService.isUserActivated(email);
			if (!activated) {
				return new ResponseEntity<>(USER_NOT_ACTIVATED, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>(userClient.resetPassword(request), HttpStatus.OK);
		} catch (final UserGridException exp) {
			LOG.error("Error to resetPw {}", exp);
			throw new InvalidEmailException(ErrorCode.REQUEST_INVALID.withDetail(exp.getMessage()), exp);
		}
	}

	@RequestMapping(value = "/reactivate", method = POST)
	public ResponseEntity reActivate(final @RequestBody Map<String, Object> body) {

		String email = (String) body.get("email");
		final String facebookId = (String) body.get("fbid");

		if (StringUtils.isEmpty(email) && !StringUtils.isEmpty(facebookId)) {
			email = userManagementService.findEmailByFacebookId(facebookId).orElse(null);
		}

		if (StringUtils.isEmpty(email)) {
			throw new ConstraintViolationException("Email is required for reactivation", Collections.emptySet());
		}

		final boolean activated = userManagementService.isEmailActivated(email);
		if (activated) {
			return new ResponseEntity<>(USER_IS_ACTIVATED, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(userClient.reActivate(email), HttpStatus.OK);
	}

	@RequestMapping(value = "/activated", method = GET, produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> checkActivated(@RequestParam(required = false) final String email,
			@RequestParam(required = false) final String fbid) {

		final boolean activated;
		if (!StringUtils.isEmpty(email)) {

			activated = userManagementService.isUserActivated(email);
		} else if (!StringUtils.isEmpty(fbid)) {

			activated = userManagementService.isFacebookActivated(fbid);
		} else {

			throw new ConstraintViolationException("Email or facebook id is required for checking activation status",
					Collections.emptySet());
		}
		return Collections.singletonMap(ACTIVATED, activated);
	}

	@RequestMapping(value = "/ejabberd/check_password", method = GET, produces = TEXT_PLAIN_VALUE)
	public String ejabberCheckPassword(@RequestParam(required = false) final String user,
			@RequestParam(required = false) final String pass) {
		try {
			return userClient.getUserByToken(user) != null ? TRUE : FALSE;
		} catch (final Exception ex) {
			LOG.warn("Error on get user by token", ex);
			return FALSE;
		}
	}

	@RequestMapping(value = "/ejabberd/user_exists", method = GET, produces = TEXT_PLAIN_VALUE)
	public String ejabberUserExists(@RequestParam(required = false) final String user) {
		try {
			return !userManagementService.isUserNameExisted(user) ? FALSE : TRUE;
		} catch (final UserGridException uge) {
			LOG.error("Error on checking user existed.", uge);
			return FALSE;
		}
	}

	@RequiredAdminRole
	@RequestMapping(value = "/admin/activate", method = POST, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity adminActivateUser(@RequestBody final AdminActivateRequest request) {
		if (!userService.activateUser(request)) {
			return new ResponseEntity<>(
					ErrorCode.INTERNAL_SERVICE_ERROR.withError("FAILED_TO_ACTIVATED", "Failed to activate user."),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, Boolean.TRUE), HttpStatus.OK);
	}

	@RequestMapping(value = "/register/{uuid}/confirm", method = GET, produces = APPLICATION_JSON_VALUE)
	public Map<String, Object> userConfirmRegistration(@PathVariable("uuid") final String uuid,
			@RequestParam("token") final String token, final HttpServletRequest request) throws IOException {

		return Collections.singletonMap(SUCCESS, userService.userConfirmEmail(uuid));
	}

	@RequiredAdminRole
	@RequestMapping(value = "/changeExtendUserType", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> changeExtendUserType(
			final @Validated @RequestBody ChangeExtendUserTypeRequest body) {

		boolean result = userService.adminChangeExtendUserType(body);

		return new ResponseEntity<>(Collections.singletonMap(SUCCESS, result), HttpStatus.OK);
	}

	@RequestMapping(value = "/users/update-phone", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updatePhoneWithOTP(final Account account,
			@RequestHeader(value = "Accept-Language") String language, @RequestBody PhoneNumberDTO phoneRequest)
			throws Exception {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			Boolean result = userService.updatePhone(account.uuid(), phoneRequest.getPhoneNumber());
			if (result == false) {
				LOG.error("updatePhoneWithOTP: Can not update phone_number to DB");
				apiResponse.setStatus(ErrorCode.FAILED);
				apiResponse.setData("Can not update phone_number to DB");
				return new ResponseEntity<>(apiResponse, HttpStatus.OK);
			}

			userClient.updatePhoneNumber(account.uuid(), phoneRequest);
			Integer code = otpService.getOTPCode(account.uuid() + phoneRequest.getPhoneNumber());
			if (code == null) {
				LOG.error("sendOtpSMS: Can not generate OTP code");
				apiResponse.setStatus(ErrorCode.FAILED);
				apiResponse.setData("Can not generate OTP code");
				return new ResponseEntity<>(apiResponse, HttpStatus.OK);
			}
			String finalCode = String.format("%0" + 6 + "d", code);

			return otpService.sendSmsVerificationPhone(account.uuid(), finalCode, language);
		} catch (Exception e) {
			throw new Exception("updatePhoneWithOTP: " + e.getMessage());
		}
	}

}
