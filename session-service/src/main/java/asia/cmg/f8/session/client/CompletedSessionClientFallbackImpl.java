package asia.cmg.f8.session.client;

import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.entity.CompletedSessionEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created on 12/15/16.
 */
@Component
public class CompletedSessionClientFallbackImpl implements CompletedSessionClient {

    @Override
    public UserGridResponse<CompletedSessionEntity> getAllCompletedSessionsNotRated(
            @RequestParam("query") final String query) {
        throw new UserGridException("ERROR_ON_GET_NOT_RATED_SESSIONS",
                "Usergrid went wrong while getting all completed session of EU query: " + query);
    }

    @Override
    public UserGridResponse<CompletedSessionEntity> findOneCompletedSessions(
            @RequestParam("query") final String query) {
        throw new UserGridException("ERROR_ON_GET_COMPLETED_SESSIONS_FOR_RATING",
                "Usergrid went wrong while finding one completed session by query: " + query);
    }

    @Override
    public UserGridResponse<CompletedSessionEntity> createCompletedSession(
            @RequestBody final CompletedSessionEntity entity) {
    	return new UserGridResponse<CompletedSessionEntity>();
    }

    @Override
    public UserGridResponse<CompletedSessionEntity> updateCompletedSession(
            @PathVariable("uuid") final String uuid,
            @RequestBody final CompletedSessionEntity entity) {
        return new UserGridResponse<CompletedSessionEntity>();
    }

	@Override
	public UserGridResponse<CompletedSessionEntity> getCompletedSessionsByQueryAndLimit(
			@RequestParam("query")final String query, 
			@RequestParam("limit")final int limit) {
		return new UserGridResponse<CompletedSessionEntity>();
	}

	@Override
	public UserGridResponse<CompletedSessionEntity> getByUuid(@PathVariable("uuid") final String uuid) {
		return new UserGridResponse<CompletedSessionEntity>();
	}
}
