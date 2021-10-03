package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.UnreadCounter;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created on 1/19/17.
 */
@FeignClient(value = "counters", url = "${counter.url}", fallback = CounterClientFallback.class)
public interface CounterClient {

    String COUNTER_NAME = "counterName";

    @RequestMapping(method = GET, path = "/counters/{counterName}", produces = APPLICATION_JSON_VALUE)
    UnreadCounter getUnReadCounter(@PathVariable(COUNTER_NAME) String counterName);

    @RequestMapping(method = PUT, path = "/counters/{counterName}/increase")
    UserGridResponse<Map<String, Object>> increaseCounter(@PathVariable(COUNTER_NAME) final String name);

    @RequestMapping(method = PUT, path = "/counters/{counterName}/decrease")
    UserGridResponse<Map<String, Object>> decreaseCounter(@PathVariable(COUNTER_NAME) final String name);

    @RequestMapping(method = DELETE, path = "/counters/{counterName}")
    Map<String, Object> resetCounter(@PathVariable(COUNTER_NAME) final String name);
}
