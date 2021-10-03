package asia.cmg.f8.report.client;

import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.report.config.UserGridConfig;
import asia.cmg.f8.report.dto.Counter;
import asia.cmg.f8.report.dto.CounterRequest;
import asia.cmg.f8.report.dto.CounterResponseWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(value = "counters", url = "${feign.url}"/*, fallback = CounterClientFallbackImpl.class*/,
        configuration = UserGridConfig.class)
public interface CounterClient {
    String COUNTER = "counter";
    String COMPLETE_COUNTER = "completeCounter";
    String NOSHOW_COUNTER = "noShowCounter";
    String USERCANCEL_COUNTER = "userCancelCounter";

    String TOTAL_ORDER_COUNTER = "totalOrderCounter";
    String TOTAL_SESSION_COUNTER = "totalSessionCounter";
    
    String SECRET_QUERY = "client_id=${userGrid.clientId}&client_secret=${userGrid.clientSecret}";

    @RequestMapping(
            value = "/events?" + SECRET_QUERY,
            method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    UserGridResponse<?> updateCounter(@RequestBody final CounterRequest request);

    @RequestMapping(
            value = "/counters?counter={counter}&" + SECRET_QUERY,
            method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    CounterResponseWrapper<Counter> getCounter(@PathVariable(COUNTER) final String counter);

    @RequestMapping(
            value = "/counters?start_time={startTime}&end_time={endTime}&resolution={resolution}"
                    + "&counter={counter}&" + SECRET_QUERY,
            method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    CounterResponseWrapper<Counter> getCounterByRange(
            @PathVariable("startTime") final long startTime,
            @PathVariable("endTime") final long endTime,
            @PathVariable("resolution") final String resolution,
            @PathVariable(COUNTER) final String counter);

    @RequestMapping(
            value = "/counters?start_time={startTime}&end_time={endTime}&resolution={resolution}"
                    + "&counter={completeCounter}&counter={noShowCounter}&counter={userCancelCounter}&" + SECRET_QUERY,
            method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    CounterResponseWrapper<Counter> getAllCounterByRange(
            @PathVariable("startTime") final long startTime,
            @PathVariable("endTime") final long endTime,
            @PathVariable("resolution") final String resolution,
            @PathVariable(COMPLETE_COUNTER) final String completeCounter,
            @PathVariable(NOSHOW_COUNTER) final String noShowCounter,
            @PathVariable(USERCANCEL_COUNTER) final String userCancelCounter);

    @RequestMapping(
            value = "/counters?start_time={startTime}&end_time={endTime}&resolution={resolution}"
                    + "&counter={totalOrderCounter}&counter={totalSessionCounter}&" + SECRET_QUERY,
            method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    CounterResponseWrapper<Counter> getOrderCounterByRange(
            @PathVariable("startTime") final long startTime,
            @PathVariable("endTime") final long endTime,
            @PathVariable("resolution") final String resolution,
            @PathVariable(TOTAL_ORDER_COUNTER) final String totalOrderCounter,
            @PathVariable(TOTAL_SESSION_COUNTER) final String totalSessionCounter);
}
