package asia.cmg.f8.session.service;

import asia.cmg.f8.session.dto.SessionNotificationInfo;

import java.util.List;

/**
 * Created on 4/18/17.
 */
public interface SessionNotificationService {

    List<SessionNotificationInfo> searchForNotification(final long fromTime, final long toTime);
}
