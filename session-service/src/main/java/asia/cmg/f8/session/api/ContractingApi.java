package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.session.dto.ContractUser;
import asia.cmg.f8.session.dto.PageResponse;
import asia.cmg.f8.session.dto.TrainerClient;
import asia.cmg.f8.session.service.ContractingService;
import asia.cmg.f8.session.service.OrderService;

/**
 * Created on 12/7/16.
 */
@RestController
public class ContractingApi {
	private static final String ALLTRAINERS = "alltrainers";
	private static final String TRAINER_ID = "trainerId";
	private static final String CONTRACTING = "contracting";
	private static final String USER_ID = "userId";
	public static final String CURRENT_USER = "me";
	private final static String KEYWORD = "keyword";

	private final ContractingService contractingService;
	private final OrderService orderService;

	public static final Logger LOGGER = LoggerFactory.getLogger(ContractingApi.class);

	@Inject
	public ContractingApi(final ContractingService contractingService, final OrderService orderService) {
		this.contractingService = contractingService;
		this.orderService = orderService;
	}

	/**
	 * Current User Search their contracting user
	 *
	 * @param account
	 *            current User
	 * @param keyword
	 *            search keyword
	 * @return List of Contracting User of current PT
	 */
	@RequestMapping(value = "/contracting", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public List<ContractUser> searchContractingUser(@RequestParam(value = "keyword", defaultValue = "") final String keyword, 
													@RequestParam(value = "allcontracts", defaultValue = "false") final boolean allContracts, 
													final Account account) {

		List<ContractUser> users = Collections.emptyList();
		LOGGER.info("-- contract users {}", account.isEu());
		if (account.isEu()) {
			users = contractingService.searchContractingPt(account.uuid(), allContracts, keyword);
		} else if (account.isPt()) {
			users = contractingService.searchContractingEu(account, allContracts, keyword);
		}
		return users;
	}

	@RequestMapping(value = "/pts/excludecontracting", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PageResponse<ContractUser> searchPTsExcludingContracting(
			@RequestParam(value = KEYWORD, defaultValue = "") final String keyword,
			@RequestParam(value = ALLTRAINERS, required = false, defaultValue = "false") final Boolean alltrainers,
			@PageableDefault(sort = { "full_name" }) final Pageable pageable, final Account account) {
		LOGGER.info("---- alltrainers = {}", alltrainers);
		return contractingService.searchPTsExcludingContracting(account, keyword, pageable, alltrainers);
	}
	
	@RequestMapping(value = "/pts/excludecontracting/change_trainer", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PageResponse<ContractUser> searchPTsSameLevelForExcludingContracting(
			@RequestParam(value = KEYWORD, defaultValue = "") final String keyword,
			@RequestParam(value = ALLTRAINERS, required = false, defaultValue = "false") final Boolean alltrainers,
			@RequestParam(value = "userLevel") final String userLevel,
			@PageableDefault(sort = { "full_name" }) final Pageable pageable, final Account account) {
		LOGGER.info("---- alltrainers = {}", alltrainers);
		return contractingService.searchPTsSameLevelExcludingContracting(account, keyword, userLevel, pageable, alltrainers);
	}

	@RequestMapping(value = "/sessions/users/{userId}/contracting/trainers/{trainerId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public Map<String, Object> checkContractingWithTrainer(@PathVariable(USER_ID) final String userId,
			@PathVariable(TRAINER_ID) final String trainerId, final Account account) {
		String userUuid = userId;
		if (userId.equalsIgnoreCase(CURRENT_USER)) {
			userUuid = account.uuid();
		}

		return Collections.singletonMap(CONTRACTING, orderService.isContracting(userUuid, trainerId));
	}

	/**
	 * PT Search their client user both past and current.
	 *
	 * @param account
	 *            current User
	 * @param keyword
	 *            search keyword
	 * @return List of Contracting User of current PT
	 */
	@RequiredPTRole
	@RequestMapping(value = "/client", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public PageResponse<TrainerClient> searchPtClient(
			@RequestParam(value = "keyword", defaultValue = "") final String keyword,
			@PageableDefault(sort = "client.order_date", direction = Sort.Direction.DESC) final Pageable pageable,
			final Account account) {
		return contractingService.searchPtClient(account, keyword, pageable);
	}

	@RequestMapping(value = "/sessions/users/me/contracting/trainers/{trainerId}/history", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> getContractingWithUser(@PathVariable(TRAINER_ID) final String trainerId,
			final Account account) {
		return new ResponseEntity<>(contractingService.getContractingHistoryByTrainer(account.uuid(), trainerId),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/sessions/users/{userId}/contracting/trainers/me/history", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredPTRole
	public ResponseEntity<?> getContractingWithTrainer(@PathVariable(USER_ID) final String userId,
			final Account account) {
		return new ResponseEntity<>(contractingService.getContractingHistoryByTrainer(userId, account.uuid()),
				HttpStatus.OK);
	}
}
