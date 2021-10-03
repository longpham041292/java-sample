package asia.cmg.f8.session.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 11/24/16.
 */
public interface SessionStatusOperations {

    Logger LOG = LoggerFactory.getLogger(SessionStatusOperations.class);

    String DO_NOT_SUPPORT_PATTERN = "Don't support action {} on status {}";

    default SessionStatus book(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.RESERVE, defaultStatus());
        return sessionEntity.getStatus();
    }

    default SessionStatus checkIn(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.CHECKIN, defaultStatus());
        return sessionEntity.getStatus();
    }

    default SessionStatus noShow(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.NOSHOW, defaultStatus());
        return sessionEntity.getStatus();
    }

    default SessionStatus accept(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.ACCEPT, defaultStatus());
        return sessionEntity.getStatus();
    }

    default SessionStatus decline(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.DECLINE, defaultStatus());
        return sessionEntity.getStatus();
    }

    default SessionStatus cancel(final SessionEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.CANCEL, defaultStatus());
        return sessionEntity.getStatus();
    }

    SessionStatus defaultStatus();
}
