package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.notification.entity.SessionOrderEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import rx.Observable;

/**
 * Created by tuong.le on 1/4/17.
 */
@Component
public class SessionClientFallbackImpl implements SessionClient {
    private static final Logger LOG = LoggerFactory.getLogger(SessionClientFallbackImpl.class);

    @Override
    public Observable<SessionOrderEntity> getSessionPackageByIdObservable(
            @RequestParam(PACKAGE_ID) final String orderId) {
        LOG.error("Bad response from session service while retrieving session package {}", orderId);
        return Observable.just(new SessionOrderEntity(StringUtils.EMPTY, 0, 0));
    }

    @Override
    public CurrentTransferPackageInfo getTransferSessionPackageInfo(@RequestParam(OLD_PACKAGE_ID) final String oldPackageId, @RequestParam(NEW_PACKAGE_ID) final String newPackageId) {
        return null;
    }
}
