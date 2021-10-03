package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.client.UserClient;
import asia.cmg.f8.session.config.SessionProperties;
import asia.cmg.f8.session.dto.BookingResponse;
import asia.cmg.f8.session.dto.CheckinClubRequest;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.dto.CountDown;
import asia.cmg.f8.session.dto.PopupSchedule;
import asia.cmg.f8.session.dto.SessionPackage;
import asia.cmg.f8.session.dto.TransferRequest;
import asia.cmg.f8.session.dto.UserEntity;
import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.PagedUserResponse;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.internal.service.InternalSessionPackageManagementService;
import asia.cmg.f8.session.internal.service.InternalUserManagementService;
import asia.cmg.f8.session.operations.ClientActions;
import asia.cmg.f8.session.service.ClubService;
import asia.cmg.f8.session.service.OrderService;
import asia.cmg.f8.session.service.SessionService;
import asia.cmg.f8.session.service.UserService;
import asia.cmg.f8.session.utils.CaseInsensitiveConverter;
import asia.cmg.f8.session.utils.SessionErrorCode;

/**
 * Created on 11/22/16.
 */
@RestController
public class SessionApi {

	private static final String SESSION_ID = "sessionId";
	private static final String CLUB_UUID = "clubUuid";
	private static final String STATUS = "status";
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionApi.class);
	private static final int DEFAULT_LIMIT = 1_000;
	private static final String GET_ACTIVATED_QUERY = "select * where activated = true";
	private final ClubEntity NO_CHECKIN_CLUB = null;

	private final SessionService sessionService;
	private final InternalUserManagementService internalUserManagementService;
	private final UserClient userClient;
	private final OrderService orderService;
	private final UserService userService;
	private final SessionProperties sessionProperties;
	private final ClubService clubService;

	public static final String DATE_PATTERN = "MM/dd/yyyy";

	@Inject
	private InternalSessionPackageManagementService internalSessionPackageManagementService;

	public SessionApi(final SessionService sessionService,
			final InternalUserManagementService internalUserManagementService, final UserClient userClient,
			final OrderService orderService, final UserService userService, 
			final SessionProperties sessionProperties, final ClubService clubService) {
		this.sessionService = sessionService;
		this.internalUserManagementService = internalUserManagementService;
		this.userClient = userClient;
		this.orderService = orderService;
		this.userService = userService;
		this.sessionProperties = sessionProperties;
		this.clubService = clubService;
	}

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(ClientActions.class, new CaseInsensitiveConverter<>(ClientActions.class));
	}

	@RequestMapping(value = "/admin/sessions/{sessionId}/status/{status}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public BookingResponse updateSessionStatusOnBehalf(@PathVariable(value = SESSION_ID) final String sessionId,
			@PathVariable(value = STATUS) final ClientActions actions, final Account account) {
		return sessionService.updateSessionStatus(sessionId, actions, account, false, NO_CHECKIN_CLUB);
	}

	@RequestMapping(value = "/sessions/{sessionId}/status/{status}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
	public BookingResponse updateSessionStatus(@PathVariable(value = SESSION_ID) final String sessionId,
			@PathVariable(value = STATUS) @Valid final ClientActions actions, final Account account) {
		return sessionService.updateSessionStatus(sessionId, actions, account, true, NO_CHECKIN_CLUB);
	}

	@RequestMapping(value = "/mobile/v1/session_event/checkin_club", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> updateSessionWithCheckinClub(@RequestBody @Valid CheckinClubRequest request, final Account account) {
		ApiRespObject<Object> response = new ApiRespObject<Object>();
		
		try {
			// Check club if existed
			ClubEntity checkinClub = clubService.getClubByUuid(request.getClubUuid());
			if(Objects.isNull(checkinClub)) {
				response.setStatus(SessionErrorCode.INVALID_CLUB_ID.toErrorCode());
				return new ResponseEntity<Object>(response, HttpStatus.OK);
			}
			BookingResponse bookingResponse = sessionService.updateSessionStatus(request.getSessionUuid(), ClientActions.CHECKIN, account, true, checkinClub);
			if(bookingResponse.getResult() == false) {
				response.setStatus(ErrorCode.FAILED);
			} else {
				response.setData(checkinClub);
				response.setStatus(ErrorCode.SUCCESS);
			}
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/sessions/users/me/countdown", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public CountDown getCountDownSession(final Account account) {
		return sessionService.getCountDownSession(account.uuid());
	}

	@RequestMapping(value = "/sessions/schedule/users/{user_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<PopupSchedule> getScheduleMessage(@PathVariable("user_uuid") final String userUuid) {
		return new ResponseEntity<>(sessionService.getScheduleMessageByUser(userUuid), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/sessions/transfer", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<SessionPackage> transferSessions(@RequestBody final TransferRequest transferRequest) {
		return new ResponseEntity<>(sessionService.transferSessions(transferRequest.getSessionPackageUuid(),
				transferRequest.getNewTrainerUuid()), HttpStatus.OK);
	}

	@RequestMapping(value = "/internal/sessions/transfer/{oldPackageId}/{newPackageId}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<CurrentTransferPackageInfo> getTransferSessionPackageInfo(
			@PathVariable("oldPackageId") final String oldPackageId,
			@PathVariable("newPackageId") final String newPackageId) {
		return new ResponseEntity<>(sessionService.getTransferSessionPackageInfo(oldPackageId, newPackageId)
				.orElse(new CurrentTransferPackageInfo()), HttpStatus.OK);
	}

	@RequestMapping(value = "/sessions/auto-expire", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity autoSetExpiredSessions() {
		return new ResponseEntity<>(sessionService.autoExpiredSession(), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/sessions/inactive", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<String> inActiveContract(@RequestBody final Map<String, Object> body) {
		final String sessionUuid = (String) body.get("package_session_uuid");
		if (StringUtils.isEmpty(sessionUuid)) {
			return new ResponseEntity<String>("package_session_uuid must not null", HttpStatus.BAD_REQUEST);
		}
		Optional<SessionPackageEntity> sessionPackage = internalSessionPackageManagementService
				.getSessionPackageBySessionPackageUuid(sessionUuid);
		if (!sessionPackage.isPresent()) {
			return new ResponseEntity<String>("package_session_uuid must exist", HttpStatus.BAD_REQUEST);
		}

		internalSessionPackageManagementService.updateSessionPackageStatus(sessionUuid, SessionPackageStatus.INACTIVE);
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/sessions/active", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<String> activeContract(@RequestBody final Map<String, Object> body) {
		final String sessionUuid = (String) body.get("package_session_uuid");

		if (StringUtils.isEmpty(sessionUuid)) {
			return new ResponseEntity<String>("package_session_uuid must not null", HttpStatus.BAD_REQUEST);
		}

		Optional<SessionPackageEntity> sessionPackage = internalSessionPackageManagementService
				.getSessionPackageBySessionPackageUuid(sessionUuid);
		if (!sessionPackage.isPresent()) {
			return new ResponseEntity<String>("package_session_uuid must exist", HttpStatus.BAD_REQUEST);
		}
		SessionPackageStatus lastStatus = sessionPackage.get().getLastStatus();
		internalSessionPackageManagementService.updateSessionPackageStatus(sessionUuid, lastStatus);
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/users/import-missing-user-from-ug", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<String> importUserFromUG(@RequestBody String[] uuids) {
		for (int i = 0; i < uuids.length; i++) {
			try {
				internalUserManagementService.createIfNotExist(uuids[i]);
			} catch (Exception ex) {
				LOGGER.error("Can not import user", ex);
			}
		}
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/users/import-missing-user-from-mysql", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<String> importUserFromMysql(@RequestBody String[] uuids) {
		for (int i = 0; i < uuids.length; i++) {
			try {

				BasicUserEntity user = userService.findOneByUuid(uuids[i]).get();
				UserEntity userEntity = UserEntity.builder().withUuid(user.getUuid()).withEmail(user.getEmail())
						.withClubcode(user.getClubcode()).withActivated(user.getEmailValidated())
						.withLevel(user.getLevel()).withName(user.getFullName()).withUsername(user.getUserName())
						.withUsercode(user.getUsercode()).withCountry(user.getCountry())
						.withEmailvalidated(user.getEmailValidated())
						.withUserType(UserType.valueOf(user.getUserType().toUpperCase())).build();

				userClient.createUser(userEntity);
			} catch (Exception ex) {
				LOGGER.error("Can not import user", ex);
			}
		}
		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	/***
	 * Import missing users from mysql
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/admin/users/missing", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public Map<String, Object> importMissingUsers(@RequestBody final Map<String, Object> body) {

		final Optional<Long> createdMilisecond = Optional.ofNullable((Long) body.get("createdMilisecond"));
		final Optional<String> uuid = Optional.ofNullable((String) body.get("uuid"));
		if (!createdMilisecond.isPresent()) {
			throw new IllegalArgumentException("createdMilisecond is required for this action.");
		}
		try {
			String cursor = null;
			final StringBuilder query = new StringBuilder(GET_ACTIVATED_QUERY);
			query.append(String.format(" and created >= %s", createdMilisecond.get()));
			if (uuid.isPresent()) {
				query.append(String.format(" and uuid = '%s'", uuid.get()));
			}
			LOGGER.info("Query to get actived users {}", query.toString());

			PagedUserResponse<UserEntity> response = null;

			List<String> userUuids = userService.getAllUserUuids();

			do {
				if (StringUtils.isEmpty(cursor)) {
					response = userClient.getUserByQueryWithPaging(query.toString(), DEFAULT_LIMIT);
				} else {
					response = userClient.getUserByQueryWithPaging(query.toString(), DEFAULT_LIMIT, cursor);
				}
				if (null != response) {
					if (null != response.getEntities()) {
						final List<UserEntity> entities = response.getEntities();
						final List<UserEntity> missingEntities = entities.stream()
								.filter(u -> !userUuids.contains(u.getUuid())).collect(Collectors.toList());

						for (final UserEntity item : missingEntities) {
							try {
								internalUserManagementService.createIfNotExist(item.getUuid());
								LOGGER.info("Create missing users for uuid {} ", item.getUuid());
							} catch (Exception ex) {
								LOGGER.info("Error during restore user with uuid {} exeption {}", item.getUuid(),
										ex.toString());
							}
						}
					}
					cursor = response.getCursor();
					LOGGER.info("Process missing users pageSize {} cursor {}", DEFAULT_LIMIT, cursor);
				}
			} while (null != cursor && response != null);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return Collections.singletonMap("SUCCESS", Boolean.FALSE);
		}
		return Collections.singletonMap("SUCCESS", Boolean.TRUE);
		
		
	}

	@RequestMapping(value = "/internal/orders/{orderUuid}/cancel-invalid-sessions", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> internalCancelInvalidSessions(@PathVariable("orderUuid") final String orderUuid,
			@RequestParam("expired_date") final String expiredDate) {
		try {
			final FastDateFormat fdf = FastDateFormat.getInstance(DATE_PATTERN);
			final LocalDate date = fdf.parse(expiredDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			orderService.cancelInvalidSessions(date, orderUuid);
		} catch (ParseException e) {
			return new ResponseEntity<String>("expired_date is invalid", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mobile/v1/count_all_history_sessions/user/{user_uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> countAllSessionHistory(@PathVariable(name = "user_uuid") final String userUuid, final Account account) {
		ApiRespObject<Object> apiResp = new ApiRespObject<Object>();
		apiResp.setStatus(ErrorCode.SUCCESS);
		try {
			int count = sessionService.countAllHistorySession(userUuid);
			apiResp.setData(Collections.singletonMap("count", count));
		} catch (Exception e) {
			apiResp.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResp, HttpStatus.OK);
	}
	
	// TODO: after run this api on production, it should be removed.
	// add all current users to default group
	@RequestMapping(value = "/admin/users/add-default-group", method = RequestMethod.PUT)
	@RequiredAdminRole
	public ResponseEntity<String> resetDefaultGroup() {

		// add users to EU group
		final List<String> euUuids = userService.getAllUserUuidsByUserType(UserType.EU.toString());
		addUsersToGroup(euUuids, sessionProperties.getEuGroupId());

		// add users to PT group
		final List<String> ptUuids = userService.getAllUserUuidsByUserType(UserType.PT.toString());
		addUsersToGroup(ptUuids, sessionProperties.getPtGroupId());

		return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
	}

	private void addUsersToGroup(final List<String> uuids, final String groupId) {
		uuids.forEach(uuid -> {
			try {
				userClient.addUserToGroup(uuid, groupId);
			} catch (Exception ex) {
				LOGGER.error("Can not add user {} to default group {} ", uuid, ex);
			}
		});
	}
}
