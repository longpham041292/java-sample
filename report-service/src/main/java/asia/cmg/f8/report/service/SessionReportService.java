package asia.cmg.f8.report.service;

import asia.cmg.f8.report.client.CounterClient;
import asia.cmg.f8.report.dto.Counter;
import asia.cmg.f8.report.dto.CounterResponseWrapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author tung.nguyenthanh
 */
@Service
public class SessionReportService {

    @Inject
    private CounterClient counterClient;

    public CounterResponseWrapper<Counter> getStatistic(final String counter, final long startTime, final long endTime, final String resolution) {
        return counterClient.getCounterByRange(startTime, endTime, resolution, counter);
    }

    public CounterResponseWrapper<Counter> getAllStatistic(final String completeCounter,
                                                           final String noShowCounter, final String userCancelCounter, final long startTime,
                                                           final long endTime, final String resolution) {
        return counterClient.getAllCounterByRange(startTime, endTime, resolution, completeCounter,
                noShowCounter, userCancelCounter);
    }

    public CounterResponseWrapper<Counter> getOrderSessionCounter(final String totalOrderCounter,
                                                                  final String totalSessionCounter, final long startTime,
                                                                  final long endTime, final String resolution) {
        return counterClient.getOrderCounterByRange(startTime, endTime, resolution, totalOrderCounter,
                totalSessionCounter);
    }

}
