package asia.cmg.f8.notification.service.inbox;

import asia.cmg.f8.notification.client.ProfileClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created on 1/4/17.
 * @deprecated 
 */
@Service
public class ProfileService {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileService.class);
    private final ProfileClient profileClient;

    public ProfileService(final ProfileClient profileClient) {
        this.profileClient = profileClient;
    }

    public List<Object> getQuestionOfUser(final String userId) {
        final List<Object> questionResp = profileClient.getGetQuestionOfUser(userId);
        if (Objects.isNull(questionResp)) {
            LOG.error("Could not get question answer of user Id {} with user type {}", userId);
            return Collections.emptyList();
        }
        return questionResp;
    }
}
