package asia.cmg.f8.notification.service.inbox;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.client.InboxSenderClient;
import asia.cmg.f8.notification.entity.UserEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created on 1/4/17.
 */
@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final InboxSenderClient inboxSenderClient;

    public UserService(final InboxSenderClient inboxSenderClient) {
        this.inboxSenderClient = inboxSenderClient;
    }

    public Map<String, UserEntityImpl> getUserInformationByUuid(final Set<String> listUuid) {
        final String query = listUuid.stream()
                .map(user -> "uuid ='" + user + "'")
                .collect(Collectors.joining(" or "));
        final UserGridResponse<UserEntityImpl> userResp =
                inboxSenderClient.getUserInfoByQuery(String.format("select * where %s", query));
        if (Objects.isNull(userResp)) {
            return Collections.emptyMap();
        }

        return userResp
                .getEntities()
                .stream()
                .collect(Collectors.toMap(UserEntityImpl::getUuid, userEntity -> userEntity));
    }

    public Observable<Map<String, UserEntityImpl>> getUserInformationByUuidObservable(
            final Set<String> listUuid) {
        final String query = listUuid.stream()
                .map(user -> "uuid ='" + user + "'")
                .collect(Collectors.joining(" or "));
        return inboxSenderClient
                .getUserInfoByQueryObservable(String.format("select * where %s", query))
                .map(userResp -> userResp
                        .getEntities()
                        .stream()
                        .collect(Collectors.toMap(UserEntityImpl::getUuid, userEntity -> userEntity)))
                .doOnError(error -> LOG.error("Something went wrong while get user information error {}", error))
                .firstOrDefault(Collections.emptyMap());
    }
}
