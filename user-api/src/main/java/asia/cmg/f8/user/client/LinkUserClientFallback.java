package asia.cmg.f8.user.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.dto.UserAuthResponse;
import asia.cmg.f8.user.dto.UserInfoResponse;
import asia.cmg.f8.user.entity.LinkUserEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 1/10/17.
 */
@Component
public class LinkUserClientFallback implements LinkUserClient {

    private static final UserGridResponse<LinkUserEntity> EMPTY_RESPONSE;

    static {
        EMPTY_RESPONSE = new UserGridResponse<>();
        EMPTY_RESPONSE.setEntities(Collections.emptyList());
    }

    @Override
    public UserGridResponse<LinkUserEntity> findByQuery(@PathVariable("uuid") final String uuid) {
        return null;
    }

    @Override
    public UserGridResponse<LinkUserEntity> createLink(@RequestBody final List<LinkUserEntity> entities) {
        return EMPTY_RESPONSE;
    }

    @Override
    public UserGridResponse<LinkUserEntity> unLink(@PathVariable("query") final String query) {
        return EMPTY_RESPONSE;
    }

    @Override
    public UserAuthResponse doAuth(final Map<String, Object> body) {
        return null;
    }

    @Override
    public UserGridResponse<UserInfoResponse> findUserByQuery(@PathVariable("query") final String query) {
        return null;
    }
}
