package asia.cmg.f8.notification.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created on 1/10/17.
 * @deprecated 
 */
@Component
public class ProfileClientFallbackImpl implements ProfileClient {
    @Override
    public List<Object> getGetQuestionOfUser(
            @RequestParam(USER_ID) final String userId) {
        return null;
    }
}
