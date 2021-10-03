package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.ServicedeskRequest;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 12/29/16.
 */
@FeignClient(value = "serviceDesk", url = "${feign.jiraServiceDesk}",
        fallback = ServiceDeskClientFallbackImpl.class)
public interface ServiceDeskClient {
    String AUTH_HEADER = "Authorization";
    String POST_CUSTOMER_REQUEST = "/servicedeskapi/request";

    @RequestMapping(value = POST_CUSTOMER_REQUEST, method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity postCustomerRequest(@RequestHeader(AUTH_HEADER)
                                                final String base64String,
                                                     @RequestBody final ServicedeskRequest request);
}
