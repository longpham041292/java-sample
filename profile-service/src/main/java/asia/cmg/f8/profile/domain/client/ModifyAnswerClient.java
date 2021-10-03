package asia.cmg.f8.profile.domain.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@FeignClient(value = "modifyAnswers", url = "${feign.url}",
        fallback = ModifyAnswerClientFallback.class)
public interface ModifyAnswerClient {

    String QUERY = "query";
    String LIMIT = "limit";
    String SECRECT_QUERY = "client_id=${userprofile.userGridClientId}&client_secret=${userprofile.userGridClientSecret}";

    @RequestMapping(
            value = "/answers?limit={limit}&ql={query}&" + SECRECT_QUERY,
            method = RequestMethod.PUT, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    void deleteAnswersByUser(@PathVariable(QUERY) String query,
            @RequestParam(LIMIT) int limit, @RequestBody Map<String, Boolean> updateData);


}
