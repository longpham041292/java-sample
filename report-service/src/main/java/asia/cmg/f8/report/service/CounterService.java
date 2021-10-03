package asia.cmg.f8.report.service;

import asia.cmg.f8.report.client.CounterClient;
import asia.cmg.f8.report.dto.CounterRequest;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;

@Service
public class CounterService {

    @Inject
    private CounterClient counterClient;

    public void updateCounter(final String counterName, final int quantity, final long timestamp) {
        final CounterRequest request = new CounterRequest();
        request.setTimestamp(timestamp);
        request.setCounters(Collections.singletonMap(counterName, quantity));

        counterClient.updateCounter(request);
    }
}
