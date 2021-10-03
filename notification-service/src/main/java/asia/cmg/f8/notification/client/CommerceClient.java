package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.Order;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author tung.nguyenthanh
 */
@FeignClient(value = "commerce", url = "${feign.commerce}",
        fallback = CommerceClientFallbackImpl.class)
public interface CommerceClient {

    @RequestMapping(value = "/system/orders/{orderUuid}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_VALUE)
    Order getOrder(@PathVariable("orderUuid") final String orderUuid);
}
