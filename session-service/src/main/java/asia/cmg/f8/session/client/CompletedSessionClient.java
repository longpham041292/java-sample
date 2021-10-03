package asia.cmg.f8.session.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.session.entity.CompletedSessionEntity;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 12/6/16.
 */
@FeignClient(value = "completedSession", url = "${feign.url}",
        fallback = CompletedSessionClientFallbackImpl.class)
public interface CompletedSessionClient {
    String SECRET_QUERY = "client_id=${userGrid.userGridClientId}&client_secret=${userGrid.userGridClientSecret}";
    String COMPLETED_SESSION = "/completed_sessions?" + SECRET_QUERY;

    String QUERY_COMPLETED_SESSION = COMPLETED_SESSION + "&ql={query}&limit=${socials.maxSearchResult}";
    String UPDATE_COMPLETED_SESSION_IS_RATED = "/completed_sessions/{uuid}?" + SECRET_QUERY;
    String QUERY_COMPLETED_SESSIONS_LIMIT = COMPLETED_SESSION + "&ql={query}&limit={limit}";
    String GET_BY_UUID = "/completed_sessions/{uuid}?" + SECRET_QUERY;

    @RequestMapping(value = QUERY_COMPLETED_SESSION, method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> getAllCompletedSessionsNotRated(
            @RequestParam("query") final String query);
    
    @RequestMapping(value = QUERY_COMPLETED_SESSIONS_LIMIT, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> getCompletedSessionsByQueryAndLimit(
            @PathVariable("query") final String query,
            @PathVariable("limit") final int limit);

    @RequestMapping(value = QUERY_COMPLETED_SESSION, method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> findOneCompletedSessions(
            @RequestParam("query") final String query);

    @RequestMapping(value = COMPLETED_SESSION, method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> createCompletedSession(
            @RequestBody CompletedSessionEntity entity);

    @RequestMapping(value = UPDATE_COMPLETED_SESSION_IS_RATED, method = RequestMethod.PUT,
            produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> updateCompletedSession(
            @PathVariable("uuid") final String uuid,
            @RequestBody CompletedSessionEntity entity);
    
    @RequestMapping(value = GET_BY_UUID, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    UserGridResponse<CompletedSessionEntity> getByUuid(@PathVariable("uuid") final String uuid);
}
