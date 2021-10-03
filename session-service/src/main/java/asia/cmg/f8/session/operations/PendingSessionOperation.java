package asia.cmg.f8.session.operations;

import java.time.LocalDateTime;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 11/24/18.
 */
public class PendingSessionOperation implements SessionStatusOperations {

	@Override
	public SessionStatus book(final SessionEntity sessionEntity, final Account account) {
		if (sessionEntity.getStartTime().compareTo(LocalDateTime.now()) > 0) {
			throw new IllegalArgumentException("Can not book from PENDING status when this session is not expired.");
		}
		return SessionStatus.PENDING;
	}

	@Override
	public SessionStatus accept(final SessionEntity sessionEntity, final Account account) {
		if (isActionDenied(sessionEntity, account)) {
			return SessionStatus.PENDING;
		}
		return SessionStatus.CONFIRMED;
	}

	@Override
	public SessionStatus decline(final SessionEntity sessionEntity, final Account account) {
		if (isActionDenied(sessionEntity, account)) {
			return SessionStatus.PENDING;
		}
		return SessionStatus.OPEN;
	}

	@Override
	public SessionStatus cancel(final SessionEntity sessionEntity, final Account account) {
		if (account.type() != null && account.type().equalsIgnoreCase(sessionEntity.getBookedBy())) {
			return SessionStatus.OPEN;
		}
		return SessionStatus.PENDING;
	}

	@Override
	public SessionStatus defaultStatus() {
		return SessionStatus.PENDING;
	}

	@Override
	public SessionStatus checkIn(final SessionEntity sessionEntity, final Account account) {
		if (!account.isAdmin()) {
			throw new IllegalArgumentException("End User can not do this action.");
		}

		return SessionStatus.COMPLETED;
	}

	@Override
	public SessionStatus noShow(final SessionEntity sessionEntity, final Account account) {
		if (!account.isAdmin()) {
			throw new IllegalArgumentException("End User can not do this action.");
		}

		return SessionStatus.BURNED;
	}

	private boolean isActionDenied(final SessionEntity sessionEntity, final Account account) {
		if (account.isAdmin()) {
			return false;
		}
		return account.type() != null && account.type().equalsIgnoreCase(sessionEntity.getBookedBy());
	}
}
