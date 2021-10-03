package asia.cmg.f8.profile.api.profile;

/**
 * Created on 26/10/17.
 */

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.profile.domain.service.AutoFollowService;


@RestController
public class AutoFollowApi {

	private static final Logger LOG = LoggerFactory.getLogger(AutoFollowApi.class);

	@Autowired
	private AutoFollowService autoFollowService;

	@RequestMapping(value = "/profile/updateautofollow/users/{user_uuid}/{kate_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> updateautofollowForkateUser(@PathVariable("user_uuid") final String userUuid,@PathVariable("kate_uuid") final String kateUuid,
			final Account account) {

		LOG.info("user uuid = {}",userUuid);
		return new ResponseEntity<>(autoFollowService.updateKateAutoFollow(userUuid,kateUuid ,account), HttpStatus.OK);

	}

	@RequestMapping(value = "/profile/updateautofollow/update/{user_type}/{kate_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getScheduleMessage(@PathVariable("user_type") final String userType,@PathVariable("kate_uuid") final String kateUuid,
			final Account account) {

		LOG.info("user Type = {}",userType);
		return new ResponseEntity<Map<String, Object>>(Collections.singletonMap(
                 "update", autoFollowService.updateAutofollowingByuserType(userType,kateUuid,account)),HttpStatus.OK);

	}

}
