package asia.cmg.f8.session.operations;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 11/24/16.
 */
public class CompletedSessionOperation implements SessionStatusOperations {

	@Override
	public SessionStatus defaultStatus() {
		return SessionStatus.COMPLETED;
	}

	@Override
	public SessionStatus noShow(final SessionEntity sessionEntity, final Account account) {
		if (!account.isAdmin()) {
			throw new IllegalArgumentException("End User can not do this action.");
		}
		return SessionStatus.BURNED;
	}
}
