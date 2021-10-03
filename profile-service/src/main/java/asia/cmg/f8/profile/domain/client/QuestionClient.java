package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.QuestionEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import rx.Observable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by tri.bui on 10/25/16.
 */
@FeignClient(value = "questions", url = "${usergrid.baseUrl}")
public interface QuestionClient {
    String SECRECT_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    String PUT_QUESTION = "/questions/{uuid}?" + SECRECT_QUERY;
    String QUERY = "query";

    @RequestMapping(value = "/questions?limit={limit}&ql={query}&" + SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<QuestionEntity> getQuestions(@PathVariable(QUERY) final String query, @PathVariable("limit") final int limit);

//    @RequestMapping(value = "/questions?limit={limit}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
//    Observable<UserGridResponse<QuestionEntity>> getQuestionsAsync(@RequestParam("access_token") String token, @PathVariable(QUERY) final String query, @PathVariable("limit") final int limit);

//    @RequestMapping(value = "/questions?ql={query}", method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<QuestionEntity> updateQuestion(@RequestParam("access_token") String token, @PathVariable(QUERY) final String query, @RequestBody final Object question);

    @RequestMapping(value = "/questions?ql={query}&limit={limit}&" + SECRECT_QUERY,
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<QuestionEntity> getQuestionsBySystem(
            @PathVariable("query") final String query,
            @PathVariable("limit") final int limit);
    
    @RequestMapping(value = PUT_QUESTION, method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<QuestionEntity> updateQuesttion(@PathVariable("uuid") final String uuid, @RequestBody final QuestionEntity entiy);
}
