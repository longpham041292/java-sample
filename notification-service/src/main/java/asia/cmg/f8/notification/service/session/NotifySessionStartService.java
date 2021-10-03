package asia.cmg.f8.notification.service.session;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.notification.client.SessionNotificationClient;
import asia.cmg.f8.notification.client.SessionNotificationClient.SessionNotificationInfo;
import asia.cmg.f8.notification.config.NotificationProperties;
import asia.cmg.f8.notification.dto.BasicUserInfo;
import asia.cmg.f8.notification.dto.SocialUserInfo;
import asia.cmg.f8.notification.enumeration.ENotificationEventName;
import asia.cmg.f8.notification.push.NotificationSender;
import asia.cmg.f8.notification.push.SessionTransferredNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 4/18/17.
 */
@Component
public class NotifySessionStartService extends NotificationSender {

    private static final String EU_MESSAGE_KEY = "message.session.notification.starttime.eu";
    private static final String PT_MESSAGE_KEY = "message.session.notification.starttime.pt";


    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySessionStartService.class);

    private static final int DEFAULT_TIME_PERIOD = 30; // 30 minutes
    private static final SocialUserInfo NO_AVATAR = null;
	private static final List<SocialUserInfo> NO_TAGGED_ACCOUNTS = Collections.emptyList();

    private final NotificationProperties notificationProperties;
    private final SessionNotificationClient notificationClient;

    public NotifySessionStartService(final NotificationProperties notificationProperties, final SessionNotificationClient notificationClient) {
        this.notificationProperties = notificationProperties;
        this.notificationClient = notificationClient;
    }

    @Async
    public void triggerNotifySessionStart() {

        final LocalDateTime fromTime = LocalDateTime.now().plus(5, ChronoUnit.MINUTES);
        final LocalDateTime toTime = fromTime.plus(getTimePeriodInMinutes(notificationProperties), ChronoUnit.MINUTES);
        List<SocialUserInfo> taggedUsers = new ArrayList<SocialUserInfo>();

		final List<SessionNotificationInfo> sessions = notificationClient.getEligibleSessions(
				ZoneDateTimeUtils.convertToSecondUTC(fromTime), ZoneDateTimeUtils.convertToSecondUTC(toTime));
		if (sessions != null && !sessions.isEmpty()) {

			sessions.stream().distinct().forEach(session -> {
				taggedUsers.clear();
				
				final SessionPushMessage ptMessage = createPtPushMessage(session);
				final SessionPushMessage euMessage = createEuPushMessage(session);

				final Map<String, Object> data = new HashMap<>();
				data.put("session_date", session.getStartTime());
				data.put("session_uuid", session.getSessionId());
				ptMessage.setCustomData(data);
				euMessage.setCustomData(data);

				LOGGER.info("Added session {}", session);

				Optional<BasicUserInfo> ptInfo = find(session.getPtUuid());
				SocialUserInfo taggedUser = new SocialUserInfo();
				taggedUser.setName(session.getPtName());
				taggedUser.setUuid(session.getPtUuid());
				taggedUser.setAvatar(ptInfo.isPresent() ? ptInfo.get().getPicture() : "no avatar");
				
				taggedUsers.add(taggedUser);

				sendToUser(ptMessage.getUserUuid(), ptMessage, NO_TAGGED_ACCOUNTS, NO_AVATAR, ENotificationEventName.START_SESSION.name());
				sendToUser(euMessage.getUserUuid(), euMessage, taggedUsers, taggedUser, ENotificationEventName.START_SESSION.name());

			});

		} else {
			LOGGER.info("Not found any eligible sessions for push message from {} to {}", fromTime, toTime);
		}
    }

    private SessionPushMessage createPtPushMessage(final SessionNotificationInfo session) {
        final SessionPushMessage message = new SessionPushMessage(SessionTransferredNotifier.SESSION_TYPE, session.getPtUuid(), session.getPtUuid());
        final LocalDateTime localDateTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(session.getStartTime());
        final String time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
        final String dateString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        message.setLocalizedMessage(PT_MESSAGE_KEY, time, dateString);
        return message;
    }

    private SessionPushMessage createEuPushMessage(final SessionNotificationInfo session) {
        final SessionPushMessage message = new SessionPushMessage(SessionTransferredNotifier.SESSION_TYPE, session.getUserUuid(), session.getPtUuid());
        final String ptName = session.getPtName();
        final LocalDateTime localDateTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(session.getStartTime());
        final String time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
        final String dateString = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        message.setLocalizedMessage(EU_MESSAGE_KEY, String.format("[%s]", ptName), time, dateString);
        return message;
    }

    private int getTimePeriodInMinutes(final NotificationProperties properties) {
        final Integer periodInMinutes = properties.getNotifySessionStartPeriodInMins();
        if (periodInMinutes == null) {
            return DEFAULT_TIME_PERIOD;
        }
        return periodInMinutes;
    }
}
