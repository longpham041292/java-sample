package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.entity.WhosHotConfig;
import asia.cmg.f8.session.service.WhosHotPTsService;

@RestController
public class WhoshotApi {

	public static final Logger LOGGER = LoggerFactory.getLogger(WhoshotApi.class);

	@Autowired
	private UserClient userClient;

	private final WhosHotPTsService whosHotPTsService;

	private static final String SUCCESS = "success";

	private static final String CONFIG_BY_KEY_QUERY = "select * where key = '%s'";

	@Inject
	public WhoshotApi(final WhosHotPTsService whosHotPTsService) {

		this.whosHotPTsService = whosHotPTsService;

	}

	@RequestMapping(value = "whoshot/search", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> searchWhosHot(@RequestParam(name = "nearestClubCode", defaultValue = "") final String nearestClubCode,
			@RequestParam("clubUuid") final String clubUuid, final Account account) {

		LOGGER.info("-- loging user uuid ,  {} ", account.uuid());
		LOGGER.info("-- nearestClubCode ,  {} ", nearestClubCode);
		LOGGER.info("-- ClubCodeUuid ,  {} ", clubUuid);
		return new ResponseEntity<>(
				whosHotPTsService.getWhosHotPtList(account.uuid(), nearestClubCode.toUpperCase(Locale.US), clubUuid),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/whoshot/searchConfig", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public List<WhosHotConfig> searchWhosHotConfig(@RequestParam(value = "language") final String language) {

		LOGGER.info("-- language , {} ", language);
		return whosHotPTsService.searchWhosHotConfig(language);
	}

	@RequestMapping(value = "/admin/whoshot/status", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public Map<String, Boolean> updateWhoshotConfigStatus(@RequestBody final Map<String, Object> body,
			final Account account) {
		final String configKey = (String) body.get("key");
		final boolean status = (boolean) body.get("status");

		LOGGER.info("-- status configKey -{}, ", configKey);
		LOGGER.info("-- status configKey -{}, ", status);

		userClient.updateWhosHotConfig(String.format(CONFIG_BY_KEY_QUERY, configKey),
				Collections.singletonMap("activated", status));

		return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}

	@RequestMapping(value = "/admin/whoshot/MinimumConditions", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public Map<String, Boolean> updateWhoshotConfigMinimumConditions(@RequestBody final Map<String, Object> body,
			final Account account) {
		final String configKey = (String) body.get("key");
		final Integer minimumConditions = (int) body.get("minimum_conditions");

		LOGGER.info("- MinimumConditions configKey 1 {}, ", configKey);
		LOGGER.info("- MinimumConditions  {}, ", minimumConditions);

		userClient.updateWhosHotConfig(String.format(CONFIG_BY_KEY_QUERY, configKey),
				Collections.singletonMap("minimum_conditions_value", minimumConditions));

		return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}

	@RequestMapping(value = "/admin/whoshot/weight", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public Map<String, Boolean> updateWhoshotWight(@RequestBody final Map<String, Object> body, final Account account) {
		final String configKey = (String) body.get("key");
		final Integer weight = (int) body.get("weight");

		LOGGER.info("- weight configKey {}, ", configKey);
		LOGGER.info("- weight {}, ", weight);

		userClient.updateWhosHotConfig(String.format(CONFIG_BY_KEY_QUERY, configKey),
				Collections.singletonMap("weight_value", weight));

		return Collections.singletonMap(SUCCESS, Boolean.TRUE);
	}

}
