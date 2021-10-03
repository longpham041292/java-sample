package asia.cmg.f8.profile.domain.client;

import asia.cmg.f8.profile.domain.entity.UserEntity;
import asia.cmg.f8.profile.dto.Counter;
import rx.Observable;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created on 5/23/17.
 */
@FeignClient(value = "counters", url = "${counter.url}", fallback = CounterClientFallback.class)
public interface CounterClient {

    String COUNTER_NAME = "counterName";
    String COUNTERS = "counters";

    @RequestMapping(method = GET, path = "/counters/{counterName}", produces = APPLICATION_JSON_VALUE)
    Map<String, Object> getCounter(@PathVariable(COUNTER_NAME) final String name);

    @RequestMapping(method = PUT, path = "/counters/{counterName}/increase")
    Map<String, Object> increaseCounter(@PathVariable(COUNTER_NAME) final String name);

    @RequestMapping(method = PUT, path = "/counters/{counterName}/decrease")
    Map<String, Object> decreaseCounter(@PathVariable(COUNTER_NAME) final String name);
    
    @RequestMapping(method = PUT, path = "/counters/{counterName}/random")
    Map<String, Object> randomCounter(@PathVariable(COUNTER_NAME) final String name, @RequestParam(value = "value") final int value);
    
    @RequestMapping(method = GET, path = "/counters", produces = APPLICATION_JSON_VALUE)
    Observable<List<Counter>> getCounters(@RequestParam(COUNTERS) Set<String> counterNames);

    static String buildFollowerCounterName(final UserEntity user) {
        return "FOLLOWER_" + user.getUuid();
    }
}
