package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.AnswerEntity;
import asia.cmg.f8.notification.entity.QuestionEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/12/17.
 */

@FeignClient(value = "questionAnswers", url = "${feign.url}",
        fallback = QuestionAnswerClientFallbackImpl.class)
public interface QuestionAnswerClient {
    String SECRET_QUERY = "client_id=${usergrid.clientId}&client_secret=${usergrid.clientSecret}";
    String QUERY = "query";

    @RequestMapping(value = "/questions?" + SECRET_QUERY + "&limit=${notification.maxSearchResult}&ql={query}",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<QuestionEntity> getQuestionsByQuery(@PathVariable(QUERY) final String query);

    @RequestMapping(value = "/answers?" + SECRET_QUERY + "&limit=${notification.maxSearchResult}&ql={query}",
            method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<AnswerEntity> getAnswersByQuery(@PathVariable(QUERY) final String query);

}
