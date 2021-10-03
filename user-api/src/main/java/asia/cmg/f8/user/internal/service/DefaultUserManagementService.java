package asia.cmg.f8.user.internal.service;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.user.client.UserClient;
import asia.cmg.f8.user.entity.UserEntity;
import asia.cmg.f8.user.service.UserManagementService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created on 2/21/17.
 */
@Service
public class DefaultUserManagementService implements UserManagementService {

    private final UserClient userClient;

    public DefaultUserManagementService(final UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public Optional<UserEntity> findByQuery(final String query) {
        return findFirst(userClient.getUserByQuery(query));
    }

    private Optional<UserEntity> findFirst(final UserGridResponse<UserEntity> response) {
        if (response != null && response.getEntities() != null && !response.getEntities().isEmpty()) {
            return Optional.ofNullable(response.getEntities().iterator().next());
        }
        return Optional.empty();
    }
}
