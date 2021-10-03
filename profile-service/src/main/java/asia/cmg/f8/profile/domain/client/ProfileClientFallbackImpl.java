package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created on 1/6/17.
 */
@Component
public class ProfileClientFallbackImpl implements ProfileClient {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileClientFallbackImpl.class);
    @Override
    public UserGridResponse<UserEntity> updateUserProfile(@PathVariable("uuid") final String uuid,
                                                          @RequestBody final UserEntity profile) {
        LOG.error("Exeption occur while updating profile");
        return null;
    }
}
