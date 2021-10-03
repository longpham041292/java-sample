package asia.cmg.f8.session.api;

import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.CompletedSessionResponse;
import asia.cmg.f8.session.service.CompletedSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 12/15/16.
 */
@RestController
public class CompletedSessionApi {
    private final CompletedSessionService completedSessionService;

    public CompletedSessionApi(final CompletedSessionService completedSessionService) {
        this.completedSessionService = completedSessionService;
    }

    @RequestMapping(value = "/mobile/v1/completed_sessions", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public List<CompletedSessionResponse> getListCompletedSession(final Account account) {
    	List<CompletedSessionResponse> completedSessions = new ArrayList<CompletedSessionResponse>();
    	CompletedSessionResponse completedSession = null;
    	
        if (account.isEu()) {
        	completedSession = completedSessionService.getLatestCompletedSessionEuNotRated(account);
        } else if (account.isPt()) {
        	completedSession = completedSessionService.getLatestCompletedSessionPtNotRated(account);
        }
        
        if(!Objects.isNull(completedSession)) {
        	completedSessions.add(completedSession);
        }
        
        return completedSessions;
    }
    
    @RequestMapping(value = "/mobile/v1/completed_sessions/skip_rating/{session_id}", method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity skipRatingSession(@PathVariable("session_id") final String session_id, final Account account) {
    	ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
    	
    	try {
    		completedSessionService.skipRatingCompletedSession(session_id, account);
    		apiResponse.setStatus(ErrorCode.SUCCESS);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
    	
    	return new ResponseEntity(apiResponse, HttpStatus.OK);
    }
}
