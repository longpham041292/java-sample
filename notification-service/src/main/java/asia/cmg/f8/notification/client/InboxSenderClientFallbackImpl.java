package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.notification.entity.UserEntityImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import rx.Observable;

/**
 * Created by on 1/4/17.
 */
@Component
public class InboxSenderClientFallbackImpl implements InboxSenderClient {

    @Override
    public UserGridResponse<UserEntityImpl> getUserInfoByQuery(
            @PathVariable(QUERY) final String query) {
        return null;
    }

    @Override
    public Observable<UserGridResponse<UserEntityImpl>> getUserInfoByQueryObservable(
            @PathVariable(QUERY) final String query) {
        return Observable.empty();
    }
}
