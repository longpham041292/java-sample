package asia.cmg.f8.notification.client;

import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.notification.entity.SessionOrderEntity;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import rx.Observable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created on 1/4/17.
 */

@FeignClient(value = "session", url = "${feign.session}",
        fallback = SessionClientFallbackImpl.class)
public interface SessionClient {
    String PACKAGE_ID = "package_id";
    String OLD_PACKAGE_ID = "oldPackageId";
    String NEW_PACKAGE_ID = "newPackageId";

    @RequestMapping(value = "/system/packages", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    Observable<SessionOrderEntity> getSessionPackageByIdObservable(
            @RequestParam(PACKAGE_ID) final String packageId);

    @RequestMapping(value = "/internal/sessions/transfer/{oldPackageId}/{newPackageId}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    CurrentTransferPackageInfo getTransferSessionPackageInfo(@RequestParam(OLD_PACKAGE_ID) final String oldPackageId,
                                                             @RequestParam(NEW_PACKAGE_ID) final String newPackageId);

}
