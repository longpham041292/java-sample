package asia.cmg.f8.notification.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/4/17.
 * @deprecated 
 * 
 */

@FeignClient(value = "profile", url = "${feign.profile}",
        fallback = ProfileClientFallbackImpl.class)
public interface ProfileClient {
    String USER_ID = "user_id";

    @RequestMapping(value = "/system/questions", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    List<Object> getGetQuestionOfUser(
            @RequestParam(USER_ID) final String userId);

}
