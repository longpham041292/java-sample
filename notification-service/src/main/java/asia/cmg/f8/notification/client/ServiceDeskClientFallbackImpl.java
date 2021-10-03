package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.ServicedeskRequest;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collections;

/**
 * Created on 12/29/16.
 */
@Component
public class ServiceDeskClientFallbackImpl implements ServiceDeskClient {

    private static final Logger LOG = Logger.getLogger(ServiceDeskClientFallbackImpl.class);

    @Override
    public ResponseEntity postCustomerRequest(@RequestHeader(AUTH_HEADER)
                                                                        final String base64String,
                                                            @RequestBody final ServicedeskRequest request) {
        LOG.error("Request timeout while waiting for jira response");
        return new ResponseEntity<>(Collections.singletonMap("success", Boolean.TRUE), HttpStatus.CREATED);
    }
}
