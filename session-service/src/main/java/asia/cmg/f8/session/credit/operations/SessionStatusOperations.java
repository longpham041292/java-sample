package asia.cmg.f8.session.credit.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditSessionBookingEntity;
import asia.cmg.f8.session.operations.ClientActions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 11/24/16.
 */
public interface SessionStatusOperations {

    Logger LOG = LoggerFactory.getLogger(SessionStatusOperations.class);

    String DO_NOT_SUPPORT_PATTERN = "Don't support action {} on status {}";

    default CreditBookingSessionStatus book(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.RESERVE, defaultStatus());
        return sessionEntity.getStatus();
    }

    default CreditBookingSessionStatus checkIn(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.CHECKIN, defaultStatus());
        return sessionEntity.getStatus();
    }

    default CreditBookingSessionStatus noShow(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.NOSHOW, defaultStatus());
        return sessionEntity.getStatus();
    }

    default CreditBookingSessionStatus confirm(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.ACCEPT, defaultStatus());
        return sessionEntity.getStatus();
    }

    default CreditBookingSessionStatus reject(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.DECLINE, defaultStatus());
        return sessionEntity.getStatus();
    }

    default CreditBookingSessionStatus cancel(final CreditSessionBookingEntity sessionEntity, final Account account) {
        LOG.info(DO_NOT_SUPPORT_PATTERN, ClientActions.CANCEL, defaultStatus());
        return sessionEntity.getStatus();
    }

    CreditBookingSessionStatus defaultStatus();
}
