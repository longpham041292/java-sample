package asia.cmg.f8.profile.domain.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import asia.cmg.f8.profile.dto.Counter;
import rx.Observable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 5/23/17.
 */
@Component
public class CounterClientFallback implements CounterClient {

    @Override
    public Map<String, Object> getCounter(@PathVariable(COUNTER_NAME) final String name) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> increaseCounter(@PathVariable(COUNTER_NAME) final String name) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Object> decreaseCounter(@PathVariable(COUNTER_NAME) final String name) {
        return Collections.emptyMap();
    }

	@Override
	public Observable<List<Counter>> getCounters(Set<String> counterNames) {
		return null;
	}

	@Override
	public Map<String, Object> randomCounter(String name, int value) {
		return Collections.emptyMap();	
	}
}
