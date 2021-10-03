package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.dto.BasicUserInfo;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.UserSubscriptionDto;
import asia.cmg.f8.session.service.UserSubscriptionService;

@RestController
public class UserSubscriptionApi {

	@Autowired
	private UserSubscriptionService userSubscriptionService;

	@RequestMapping(value = "/user-subscriptions", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PageResponse<UserSubscriptionDto>> getMySubscribes(
			@RequestParam(value = "active", required = true) final Boolean active,
			@PageableDefault() final Pageable pageable, final Account account) {
		
		final UserType userType = UserType.valueOf(account.type().toUpperCase());
		return new ResponseEntity<>(userSubscriptionService.getMySubscribes(account.uuid(), userType, active, pageable), HttpStatus.OK);
	}
}
