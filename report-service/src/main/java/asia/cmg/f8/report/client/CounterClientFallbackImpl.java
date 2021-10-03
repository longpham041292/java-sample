package asia.cmg.f8.report.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.dto.Counter;
import asia.cmg.f8.report.dto.CounterRequest;
import asia.cmg.f8.report.dto.CounterResponseWrapper;

public class CounterClientFallbackImpl implements CounterClient {

    @Override
    public UserGridResponse<?> updateCounter(final CounterRequest request) {
        return null;
    }

    @Override
    public CounterResponseWrapper<Counter> getCounter(final String counter) {
        return null;
    }

    @Override
    public CounterResponseWrapper<Counter> getCounterByRange(final long startTime, final long endTime, final String resolution,
                                                             final String counter) {
        return null;
    }

    @Override
    public CounterResponseWrapper<Counter> getAllCounterByRange(final long startTime, final long endTime,
                                                                final String resolution, final String completeCounter, final String noShowCounter,
                                                                final String userCancelCounter) {
        return null;
    }

    @Override
    public CounterResponseWrapper<Counter> getOrderCounterByRange(final long startTime, final long endTime,
                                                                  final String resolution, final String totalOrderCounter, final String totalSessionCounter) {
        return null;
    }

}
