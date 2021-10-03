package asia.cmg.f8.report.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.common.util.PagedUserGridResponse;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.entiy.usergrid.AnswerEntity;

@FeignClient(value = "answers", url = "${feign.url}", fallback = AnswerClientFallbackImpl.class)
public interface AnswerClient {

    String QUERY = "query";
    String LIMIT = "limit";
    String CURSOR = "cursor";
    String SECRET_QUERY = "client_id=${userGrid.clientId}&client_secret=${userGrid.clientSecret}";

    @RequestMapping(value = "/answers?" + SECRET_QUERY, method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<AnswerEntity> storeAnswerBySystem(@RequestBody AnswerEntity answer);

    @RequestMapping(value = "/answers/{answerUuid}?" + SECRET_QUERY, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<AnswerEntity> updateAnswerBySystem(@PathVariable("answerUuid") final String answerUuid, @RequestBody AnswerEntity answer);

//    @RequestMapping(value = "/answers?limit={limit}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<AnswerEntity> getAnswersByUser(@RequestParam(ACCESS_TOKEN) String token, @PathVariable(QUERY) String query, @RequestParam(LIMIT) int limit);
    
//    @RequestMapping(value = "/answers?limit={limit}&ql={query}&cursor={cursor}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
//    PagedUserGridResponse<AnswerEntity> getAnswersByUser(@RequestParam(ACCESS_TOKEN) String token, @PathVariable(QUERY) String query, @RequestParam(LIMIT) int limit, @PathVariable(CURSOR) String cursor);

//    @RequestMapping(value = "/answers?limit={limit}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
//    Observable<UserGridResponse<AnswerEntity>> getAnswersByUserAsync(@RequestParam(ACCESS_TOKEN) String token, @RequestParam(QUERY) String query, @RequestParam(LIMIT) int limit);

    @RequestMapping(value = "/answers?limit={limit}&ql={query}&" + SECRET_QUERY,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<AnswerEntity> getAnswersBySystem(@PathVariable(QUERY) final String query,
                                                      @PathVariable(LIMIT) final int answerLimit);
    
    @RequestMapping(value = "/answers?limit={limit}&ql={query}&cursor={cursor}&" + SECRET_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    PagedUserGridResponse<AnswerEntity> getAnswersBySystem(@PathVariable(QUERY) String query,
	                                                      @RequestParam(LIMIT) int limit,
	                                                      @PathVariable(CURSOR) String cursor);

}
