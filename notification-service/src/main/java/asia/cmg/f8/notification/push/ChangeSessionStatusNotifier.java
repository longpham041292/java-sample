package asia.cmg.f8.notification.push;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Component;

import asia.cmg.f8.common.event.notify.NotifyChangeSessionStatusEvent;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.notification.client.BasicUserInfoClient;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.CustomDataEventName;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.entity.InboxMessageType;
import asia.cmg.f8.notification.entity.UserGridResponse;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.util.CommonStringUtils;

/**
 * Push notification to PT when session package is changed .
 * <p>
 * Created on 1/7/17.
 */
@Component
@EnableBinding(NotifyChangeSessionStatusStream.class)
@SuppressWarnings("PMD")
public class ChangeSessionStatusNotifier extends NotificationSender {

	public static final Logger LOG = LoggerFactory.getLogger(ChangeSessionStatusNotifier.class);

	private static final String QUERY_ALL_USER_INFO = "select name, username, uuid where %s";

	public static final String MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_PENDING_MSG = "message.session.status.pt_cancelled.oldstatus.pending";
	public static final String MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_CONFIRMED_MSG = "message.session.status.pt_cancelled.oldstatus.confirmed";
	public static final String MESSAGE_SESSION_STATUS_CONFIRMED_OLDSTATUS_PENDING_MSG = "message.session.status.confirmed.oldstatus.pending";
	public static final String MESSAGE_SESSION_STATUS_CANCELLED_MSG = "message.session.status.cancelled";
	public static final String MESSAGE_SESSION_STATUS_EU_CANCELLED_MSG = "message.session.status.eu_cancelled";
	public static final String MESSAGE_SESSION_STATUS_PENDING_MSG = "message.session.status.pending";
	public static final String MESSAGE_SESSION_STATUS_OPEN_MSG = "message.session.status.open";
	public static final String MESSAGE_SESSION_BOOKING_ONBEHALF_MSG = "message.session.booking.onbehalf";
	public static final String MESSAGE_SESSION_BOOKING_ONBEHALF_ASSIGNED_PT_MSG = "message.session.booking.assigned_pt";
	public static final String MESSAGE_SESSION_STATUS_EU_COMFIRMED_MSG = "message.session.status.eu_comfirmed";
	public static final String MESSAGE_SESSION_STATUS_EU_DECLINED_MSG = "message.session.status.eu_declined";
	public static final String MESSAGE_SESSION_ADMIN_BOOKING = "message.session.admin.booking";
	public static final String MESSAGE_SESSION_ADMIN_BOOKING_CANCEL = "message.session.admin.booking.cancel";
	private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();

	@Autowired
	@Qualifier("notifyChangeSessionStatusConverter")
	private MessageConverter messageConverter;
	private final BasicUserInfoClient userInfoClient;

	public ChangeSessionStatusNotifier(final BasicUserInfoClient userInfoClient,
			final NotificationProperties notificationProperties) {
		this.userInfoClient = userInfoClient;
	}

	@StreamListener(NotifyChangeSessionStatusStream.NOTIFY_CHANGE_SESSION_STATUS_INPUT)
	public void onEvent(final Message message) {

		final NotifyChangeSessionStatusEvent event = (NotifyChangeSessionStatusEvent) messageConverter
				.fromMessage(message, NotifyChangeSessionStatusEvent.class);
		if (event == null) {
			LOG.error("NotifyChangeSessionStatusEvent is null");
			return;
		}

		if (Objects.isNull(event.getNewSessionStatus())) {
			LOG.error("New session status is null");
			return;
		}

		final SessionStatus newStatus = SessionStatus.valueOf(event.getNewSessionStatus().toString());
		final SessionStatus oldStatus = SessionStatus.valueOf(event.getOldSessionStatus().toString());

		if (Objects.isNull(newStatus) || Objects.isNull(oldStatus)) {
			LOG.error("New status and old status must be not null");
			return;
		}

		LOG.info("Change session status from Old status {} to New status {}", oldStatus.name(), newStatus.name());

		final String trainerUuid = event.getTrainerUuid().toString();
		final String euUuid = event.getEndUserUuid().toString();
		final String ownerUuid = event.getOwnerUuid() != null ? event.getOwnerUuid().toString() : StringUtils.EMPTY;
		final String conditionQuery = String.format("uuid='%s' OR uuid='%s' OR uuid='%s'", trainerUuid, euUuid, ownerUuid);
		final UserGridResponse<BasicUserInfo> usersInfoResp = userInfoClient.findBasicUserInfoByQuery(
				String.format(QUERY_ALL_USER_INFO, conditionQuery));

		if (Objects.isNull(usersInfoResp) || usersInfoResp.getEntities().isEmpty()) {
			LOG.error("Could not found trainer {} or end-user {}", trainerUuid, euUuid);
			return;
		}
		final Map<String, BasicUserInfo> userInfoMap = usersInfoResp.getEntities().stream()
				.collect(Collectors.toMap(BasicUserInfo::getUuid, basicUserInfo -> basicUserInfo));

		final Long sessionDate = event.getSessionDate();

		if (shouldSendAdminNotification(getUserType(event.getChangedBy()), newStatus)) {
			sendAdminNotification(trainerUuid, euUuid, sessionDate,
					Objects.isNull(event.getInboxMessageUuid()) ? StringUtils.EMPTY
							: event.getInboxMessageUuid().toString(),
					Objects.isNull(event.getSessionUuid()) ? StringUtils.EMPTY : event.getSessionUuid().toString(),
					newStatus);
		} else {
			SocialUserInfo trainer = new SocialUserInfo(trainerUuid, getNameFromUserMap(userInfoMap, trainerUuid), getAvatarFromUserMap(userInfoMap, trainerUuid));
			SocialUserInfo user = new SocialUserInfo(euUuid, getNameFromUserMap(userInfoMap, euUuid), getAvatarFromUserMap(userInfoMap, euUuid));
			SocialUserInfo owner = new SocialUserInfo(ownerUuid, getNameFromUserMap(userInfoMap, ownerUuid), getAvatarFromUserMap(userInfoMap, ownerUuid));
			
			sendNotification(newStatus, oldStatus, trainer, user, owner, sessionDate,
							Objects.isNull(event.getInboxMessageUuid()) ? StringUtils.EMPTY : event.getInboxMessageUuid().toString(),
							Objects.isNull(event.getSessionUuid()) ? StringUtils.EMPTY : event.getSessionUuid().toString(),
							getUserType(event.getBookedBy())
							);
		}
	}
	
	private String getNameFromUserMap(final Map<String, BasicUserInfo> userInfoMap, final String userUuid) {
		return userInfoMap.containsKey(userUuid) ? userInfoMap.get(userUuid).getName() : StringUtils.EMPTY;
	}
	
	private String getAvatarFromUserMap(final Map<String, BasicUserInfo> userInfoMap, final String userUuid) {
		return userInfoMap.containsKey(userUuid) ? userInfoMap.get(userUuid).getPicture() : StringUtils.EMPTY;
	}
	
	private String getUserType(final CharSequence userType) {
		return userType != null ? String.valueOf(userType) : null;
	}

	private void sendAdminNotification(final String trainerUuid, final String euUuid, final Long sessionDate,
			final String inboxMessageId, final String sessionUuid, final SessionStatus newStatus) {

		final Map<String, Object> data = new HashMap<>();
		data.put("session_date", sessionDate);
		data.put("session_uuid", sessionUuid);
		data.put("inbox_message_uuid", inboxMessageId);
		data.put("inbox_message_type", InboxMessageType.NEW_BOOKING.name());

		String time = StringUtils.EMPTY;
		String dateString = StringUtils.EMPTY;
		if (!Objects.isNull(sessionDate)) {
			final LocalDateTime localDateTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(sessionDate);
			time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
			dateString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		String messageKey = MESSAGE_SESSION_ADMIN_BOOKING;
		if (newStatus == SessionStatus.CANCELLED) {
			messageKey = MESSAGE_SESSION_ADMIN_BOOKING_CANCEL;
		}
		PushMessage pushMessage = createLocalizedSessionMessage(messageKey, time, dateString);
		pushMessage.setCustomData(data);
		sendToUser(euUuid, pushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.CHANGE_SESSION_STATUS.name());
		sendToUser(trainerUuid, pushMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.CHANGE_SESSION_STATUS.name());
	}

//	private void sendNotification(final SessionStatus newStatus, final SessionStatus oldStatus,
//			final String trainerUuid, final String trainerName, final String euUuid, final String euName,
//			final Long sessionDate, final String inboxMessageId, final String sessionUuid, final String bookedBy, final String ownerUuid, final String ownerName) {
//
//		String time = StringUtils.EMPTY;
//		String dateString = StringUtils.EMPTY;
//		String receiverUuid = StringUtils.EMPTY;
//		String inboxMessageType = StringUtils.EMPTY;
//
//		if (!Objects.isNull(sessionDate)) {
//			final LocalDateTime localDateTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(sessionDate);
//			time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
//			dateString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
//		}
//
//		PushMessage pushMessage = null;
//		List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
//
//		switch (newStatus) {
//		case OPEN:
//			if (oldStatus != null && !oldStatus.equals(newStatus)) {
//				if (bookedByTrainer(bookedBy)) {
//					pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_DECLINED_MSG, time,
//							dateString, CommonStringUtils.formatNameInNotification(euName));
//					receiverUuid = trainerUuid;
//					tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//				} else {
//					pushMessage = createLocalizedSessionMessage(
//							MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_PENDING_MSG, 
//							CommonStringUtils.formatNameInNotification(trainerName), time, dateString);
//					receiverUuid = euUuid;
//					tagged_accounts.add(new SocialUserInfo(trainerUuid, trainerName));
//				}
//			} else {
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_OPEN_MSG, CommonStringUtils.formatNameInNotification(euName));
//				inboxMessageType = InboxMessageType.NEW_CLIENT.name();
//				receiverUuid = trainerUuid;
//				tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//			}
//			break;
//		case PENDING:
//			if (bookedByTrainer(bookedBy)) {
//				receiverUuid = euUuid;
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_BOOKING_ONBEHALF_MSG, 
//										CommonStringUtils.formatNameInNotification(trainerName), time, dateString);
//				tagged_accounts.add(new SocialUserInfo(trainerUuid, trainerName));
//				if (!ownerUuid.equals(trainerUuid)) {
//					final Map<String, Object> data = new HashMap<>();
//					data.put("session_date", sessionDate);
//					data.put("session_uuid", sessionUuid);
//					data.put("inbox_message_uuid", inboxMessageId);
//					data.put("inbox_message_type", inboxMessageType);
//					sendMessageToAssignedPt(trainerUuid, ownerName, euUuid, euName, data);
//				}
//			} else {
//				receiverUuid = trainerUuid;
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PENDING_MSG, CommonStringUtils.formatNameInNotification(euName));
//				tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//			}
//			inboxMessageType = InboxMessageType.NEW_BOOKING.name();
//			break;
//		case EU_CANCELLED:
//			pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_CANCELLED_MSG, CommonStringUtils.formatNameInNotification(euName));
//			receiverUuid = trainerUuid;
//			inboxMessageType = InboxMessageType.CANCELLATION_WITHIN_24H.name();
//			tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//			break;
//		case CANCELLED:
//			if (SessionStatus.PENDING.equals(oldStatus)) {
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_DECLINED_MSG, time, dateString,
//						CommonStringUtils.formatNameInNotification(euName));
//			} else {
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_CANCELLED_MSG, 
//						CommonStringUtils.formatNameInNotification(euName));
//			}
//			receiverUuid = trainerUuid;
//			inboxMessageType = InboxMessageType.CANCELLATION_OUTSIDE_24H.name();
//			tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//			break;
//		case CONFIRMED:
//			if (bookedByTrainer(bookedBy)) { // it means the session is
//												// confirmed by EU then we push
//												// message to trainer
//				receiverUuid = trainerUuid;
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_COMFIRMED_MSG, time, dateString,
//						CommonStringUtils.formatNameInNotification(euName));
//				tagged_accounts.add(new SocialUserInfo(euUuid, euName));
//			} else {
//				receiverUuid = euUuid;
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_CONFIRMED_OLDSTATUS_PENDING_MSG,
//						CommonStringUtils.formatNameInNotification(trainerName), time, dateString);
//				tagged_accounts.add(new SocialUserInfo(trainerUuid, trainerName));
//			}
//			break;
//		case TRAINER_CANCELLED:
//			if (oldStatus.equals(SessionStatus.CONFIRMED)) {
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_CONFIRMED_MSG,
//						CommonStringUtils.formatNameInNotification(trainerName), time, dateString);
//				receiverUuid = euUuid;
//			} else if (oldStatus.equals(SessionStatus.PENDING)) {
//				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_PENDING_MSG,
//						CommonStringUtils.formatNameInNotification(trainerName), time, dateString);
//				receiverUuid = euUuid;
//			}
//			tagged_accounts.add(new SocialUserInfo(trainerUuid, trainerName));
//			break;
//		default:
//			throw new IllegalArgumentException(String.format("New status %s is not valid", newStatus));
//		}
//
//		if (pushMessage == null || StringUtils.isBlank(receiverUuid)) {
//			LOG.error("Could not push notification because the push message is null or missing receiver uuid");
//			return;
//		}
//
//		/**
//		 * Logic to build push message for session events.
//		 */
//
//		final Map<String, Object> data = new HashMap<>();
//		data.put("session_date", sessionDate);
//		data.put("session_uuid", sessionUuid);
//		data.put("inbox_message_uuid", inboxMessageId);
//		data.put("inbox_message_type", inboxMessageType);
//		
//		pushMessage.setCustomData(data);
//
//		sendToUser(receiverUuid, pushMessage, tagged_accounts);
//
//	}
	
	private void sendNotification(final SessionStatus newStatus, final SessionStatus oldStatus,
			final SocialUserInfo trainer, final SocialUserInfo user, final SocialUserInfo owner,
			final Long sessionDate, final String inboxMessageId, final String sessionUuid, final String bookedBy) {

		String time = StringUtils.EMPTY;
		String dateString = StringUtils.EMPTY;
		String receiverUuid = StringUtils.EMPTY;
		String inboxMessageType = StringUtils.EMPTY;

		if (!Objects.isNull(sessionDate)) {
			final LocalDateTime localDateTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(sessionDate);
			time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
			dateString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
		}

		PushMessage pushMessage = null;
		List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
		SocialUserInfo avatar = new SocialUserInfo();
		final Map<String, Object> customData = new HashMap<>();

		switch (newStatus) {
		case OPEN:
			if (oldStatus != null && !oldStatus.equals(newStatus)) {
				if (bookedByTrainer(bookedBy)) {
					pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_DECLINED_MSG, time,
							dateString, CommonStringUtils.formatNameInNotification(user.getName()));
					receiverUuid = trainer.getUuid();
					tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
					avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
				} else {
					pushMessage = createLocalizedSessionMessage(
							MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_PENDING_MSG, 
							CommonStringUtils.formatNameInNotification(trainer.getName()), time, dateString);
					receiverUuid = user.getUuid();
					tagged_accounts.add(new SocialUserInfo(trainer.getUuid(), trainer.getName()));
					avatar = SocialUserInfo.builder().uuid(trainer.getUuid()).name(trainer.getName()).picture(trainer.getAvatar()).build();
				}
			} else {
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_OPEN_MSG, CommonStringUtils.formatNameInNotification(user.getName()));
				inboxMessageType = InboxMessageType.NEW_CLIENT.name();
				customData.put("user_uuid", user.getUuid());
				customData.put("user_type", UserType.EU);
				customData.put("event_name", CustomDataEventName.PURCHASE_PACKAGE);
				receiverUuid = trainer.getUuid();
				tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
				avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
			}
			break;
		case PENDING:
			if (bookedByTrainer(bookedBy)) {
				receiverUuid = user.getUuid();
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_BOOKING_ONBEHALF_MSG, 
										CommonStringUtils.formatNameInNotification(trainer.getName()), time, dateString);
				tagged_accounts.add(new SocialUserInfo(trainer.getUuid(), trainer.getName()));
				avatar = SocialUserInfo.builder().uuid(trainer.getUuid()).name(trainer.getName()).picture(trainer.getAvatar()).build();
				
				if (!owner.getUuid().equals(trainer.getUuid())) {
					final Map<String, Object> data = new HashMap<>();
					data.put("session_date", sessionDate);
					data.put("session_uuid", sessionUuid);
					data.put("inbox_message_uuid", inboxMessageId);
					data.put("inbox_message_type", inboxMessageType);
					sendMessageToAssignedPt(trainer.getUuid(), trainer.getName(), trainer.getAvatar(), user.getUuid(), user.getName(), data);
				}
			} else {
				receiverUuid = trainer.getUuid();
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PENDING_MSG, CommonStringUtils.formatNameInNotification(user.getName()));
				tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
				avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
			}
			inboxMessageType = InboxMessageType.NEW_BOOKING.name();
			break;
		case EU_CANCELLED:
			pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_CANCELLED_MSG, CommonStringUtils.formatNameInNotification(user.getName()));
			receiverUuid = trainer.getUuid();
			inboxMessageType = InboxMessageType.CANCELLATION_WITHIN_24H.name();
			tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
			avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
			break;
		case CANCELLED:
			if (SessionStatus.PENDING.equals(oldStatus)) {
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_DECLINED_MSG, time, dateString,
						CommonStringUtils.formatNameInNotification(user.getName()));
			} else {
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_CANCELLED_MSG, 
						CommonStringUtils.formatNameInNotification(user.getName()));
			}
			receiverUuid = trainer.getUuid();
			inboxMessageType = InboxMessageType.CANCELLATION_OUTSIDE_24H.name();
			tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
			avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
			break;
		case CONFIRMED:
			if (bookedByTrainer(bookedBy)) { // it means the session is
												// confirmed by EU then we push
												// message to trainer
				receiverUuid = trainer.getUuid();
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_EU_COMFIRMED_MSG, time, dateString,
						CommonStringUtils.formatNameInNotification(user.getName()));
				tagged_accounts.add(new SocialUserInfo(user.getUuid(), user.getName()));
				avatar = SocialUserInfo.builder().uuid(user.getUuid()).name(user.getName()).picture(user.getAvatar()).build();
			} else {
				receiverUuid = user.getUuid();
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_CONFIRMED_OLDSTATUS_PENDING_MSG,
						CommonStringUtils.formatNameInNotification(trainer.getName()), time, dateString);
				tagged_accounts.add(new SocialUserInfo(trainer.getUuid(), trainer.getName()));
				avatar = SocialUserInfo.builder().uuid(trainer.getUuid()).name(trainer.getName()).picture(trainer.getAvatar()).build();
			}
			break;
		case TRAINER_CANCELLED:
			if (oldStatus.equals(SessionStatus.CONFIRMED)) {
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_CONFIRMED_MSG,
						CommonStringUtils.formatNameInNotification(trainer.getName()), time, dateString);
				receiverUuid = user.getUuid();
			} else if (oldStatus.equals(SessionStatus.PENDING)) {
				pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_STATUS_PT_CANCELLED_OLDSTATUS_PENDING_MSG,
						CommonStringUtils.formatNameInNotification(trainer.getName()), time, dateString);
				receiverUuid = user.getUuid();
			}
			tagged_accounts.add(new SocialUserInfo(trainer.getUuid(), trainer.getName()));
			avatar = SocialUserInfo.builder().uuid(trainer.getUuid()).name(trainer.getName()).picture(trainer.getAvatar()).build();
			break;
		default:
			LOG.info("Don't supporting to send notification for new status: {}", newStatus);
			return;
		}

		if (pushMessage == null || StringUtils.isBlank(receiverUuid)) {
			LOG.error("Could not push notification because the push message is null or missing receiver uuid");
			return;
		}

		/**
		 * Logic to build push message for session events.
		 */
		customData.put("session_date", sessionDate);
		customData.put("session_uuid", sessionUuid);
		customData.put("inbox_message_uuid", inboxMessageId);
		customData.put("inbox_message_type", inboxMessageType);
		
		pushMessage.setCustomData(customData);

		sendToUser(receiverUuid, pushMessage, tagged_accounts, avatar, ENotificationEventName.CHANGE_SESSION_STATUS.name());

	}
	
	private void sendMessageToAssignedPt(final String assignedPtUuid, final String ptName, final String picture, final String euUuid, final String euName,
			final Map<String, Object> data) {
		final PushMessage pushMessage = createLocalizedSessionMessage(MESSAGE_SESSION_BOOKING_ONBEHALF_ASSIGNED_PT_MSG,
																	CommonStringUtils.formatNameInNotification(ptName), 
																	CommonStringUtils.formatNameInNotification(euName));
		pushMessage.setCustomData(data);
		List<SocialUserInfo> tagged_accounts = new ArrayList<SocialUserInfo>();
		tagged_accounts.add(new SocialUserInfo(assignedPtUuid, ptName));
		tagged_accounts.add(new SocialUserInfo(euUuid, euName));
		SocialUserInfo avatar = SocialUserInfo.builder().uuid(assignedPtUuid).name(ptName).picture(picture).build();
		
		sendToUser(assignedPtUuid, pushMessage, tagged_accounts, avatar, ENotificationEventName.CHANGE_SESSION_STATUS.name());
	}

	private boolean bookedByTrainer(final String bookedBy) {
		return CommonConstant.PT_USER_TYPE.equals(bookedBy);
	}

	private boolean shouldSendAdminNotification(final String changedBy, final SessionStatus newStatus) {
		return CommonConstant.ADMIN_USER_TYPE.equals(changedBy)
				&& (newStatus.equals(SessionStatus.BURNED) || newStatus.equals(SessionStatus.CONFIRMED)
						|| newStatus.equals(SessionStatus.COMPLETED) || newStatus.equals(SessionStatus.CANCELLED));
	}

	private PushMessage createLocalizedSessionMessage(final String messageCode, final Object... args) {
		final PushMessage message = new PushMessage(SessionTransferredNotifier.SESSION_TYPE);
		message.setLocalizedMessage(messageCode, args);
		return message;
	}
	
	private PushMessage createLocalizedSessionMessage(final String messageCode, final Map<String, Object> customData, final Object... args) {
		final PushMessage message = new PushMessage(SessionTransferredNotifier.SESSION_TYPE);
		message.setCustomData(customData);
		message.setLocalizedMessage(messageCode, args);
		return message;
	}
}
