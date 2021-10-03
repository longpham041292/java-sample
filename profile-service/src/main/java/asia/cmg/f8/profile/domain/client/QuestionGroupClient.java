package asia.cmg.f8.profile.domain.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.QuestionGroupEntity;

@FeignClient(value = "questionGroups", url = "${usergrid.baseUrl}", fallback = QuestionGroupClientFallbackImpl.class)
public interface QuestionGroupClient {

	String SECRECT_QUERY = "&client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";
    final String QUERY = "query";
    final String LIMIT = "limit";
    
//    @RequestMapping(value = "/question_groups?limit={limit}&ql={query}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
//    UserGridResponse<QuestionGroupEntity> getByQueryWithLimit(final @RequestParam(ACCESS_TOKEN) String token, @PathVariable(QUERY) final String query, @PathVariable("limit") final int limit);
    
    @RequestMapping(value = "/question_groups?ql={query}&" + SECRECT_QUERY, method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    UserGridResponse<QuestionGroupEntity> getByQuery(@PathVariable(QUERY) final String query);
}
	