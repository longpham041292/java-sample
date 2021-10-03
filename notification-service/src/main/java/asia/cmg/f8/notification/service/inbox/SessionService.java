package asia.cmg.f8.notification.service.inbox;

import asia.cmg.f8.notification.client.SessionClient;
import asia.cmg.f8.notification.entity.SessionOrderEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rx.Observable;

/**
 * Created on 1/4/17.
 */
@Service
public class SessionService {
    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);

    private final SessionClient sessionClient;

    public SessionService(final SessionClient sessionClient) {
        this.sessionClient = sessionClient;
    }

    public Observable<SessionOrderEntity> getSessionPackageById(final String packageId) {
        return sessionClient
                .getSessionPackageByIdObservable(packageId)
                .doOnError(error ->
                        LOG.error("Could not get Session Package by order Id {} with error {}",
                                packageId, error))
                .firstOrDefault(new SessionOrderEntity(StringUtils.EMPTY, 0, 0));
    }
}
