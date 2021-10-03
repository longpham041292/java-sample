package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.UnreadCounter;
import asia.cmg.f8.notification.entity.UserGridResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Created on 1/19/17.
 */
@Component
public class CounterClientFallback implements CounterClient {

    @Override
    public UnreadCounter getUnReadCounter(@PathVariable(COUNTER_NAME) final String counterName) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> increaseCounter(@PathVariable(COUNTER_NAME) final String name) {
        return null;
    }

    @Override
    public UserGridResponse<Map<String, Object>> decreaseCounter(@PathVariable(COUNTER_NAME) final String name) {
        return null;
    }

    @Override
    public Map<String, Object> resetCounter(@PathVariable(COUNTER_NAME) final String name) {
        return null;
    }
}
