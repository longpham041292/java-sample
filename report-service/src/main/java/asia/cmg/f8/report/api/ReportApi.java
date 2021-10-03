package asia.cmg.f8.report.api;

import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.exception.UserGridException;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.report.TimeRange;
import asia.cmg.f8.common.spec.session.SessionStatus;
import asia.cmg.f8.common.util.NumberUtils;
import asia.cmg.f8.report.dto.Counter;
import asia.cmg.f8.report.dto.CounterResponse;
import asia.cmg.f8.report.dto.CounterResponseWrapper;
import asia.cmg.f8.report.dto.CounterValue;
import asia.cmg.f8.report.dto.TimeInfo;
import asia.cmg.f8.report.exception.InvalidTimeRangeException;
import asia.cmg.f8.report.service.SessionReportService;
import asia.cmg.f8.report.utils.DateTimeConvertUtils;
import asia.cmg.f8.report.utils.ReportConstant;
import asia.cmg.f8.report.utils.ReportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RestController
public class ReportApi {

    private static final Logger LOG = LoggerFactory.getLogger(ReportApi.class);

    @Inject
    private SessionReportService sessionReportService;

    @RequestMapping(value = "/session/{range}")
    @RequiredAdminRole
    public ResponseEntity<?> getSessionStatistic(
            @PathVariable(name = "range", required = true) final String range) {
    	TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = calculateTimeRange(timeRange);
        timeRange = ReportUtils.getQueryTimeRange(timeRange);

        final CounterResponseWrapper<Counter> response = sessionReportService.getAllStatistic(
                ReportUtils.getSessionCounterName(SessionStatus.COMPLETED.toString()),
                ReportUtils.getSessionCounterName(SessionStatus.BURNED.toString()),
                ReportUtils.getSessionCounterName(SessionStatus.EU_CANCELLED.toString()),
                timeInfo.getStartTime(), timeInfo.getEndTime(), timeRange.toString());

        return new ResponseEntity<>(processCounterResponse(response,
                timeInfo.getMiddleTime()), HttpStatus.OK);
    }

    @RequestMapping(value = "/session/package/{range}")
    @RequiredAdminRole
    public ResponseEntity<?> getPackageAverage(
            @PathVariable(name = "range", required = true) final String range,
            final LanguageContext languageContext) {
    	TimeRange timeRange = TimeRange.valueOf(range.toUpperCase(Locale.US));
        final TimeInfo timeInfo = calculateTimeRange(timeRange);
        timeRange = ReportUtils.getQueryTimeRange(timeRange);

        final CounterResponseWrapper<Counter> counterResp = sessionReportService
                .getOrderSessionCounter(ReportConstant.ORDER_COMPLETE_COUNTER,
                        ReportConstant.TOTAL_SESSION_COUNTER, timeInfo.getStartTime(),
                        timeInfo.getEndTime(), timeRange.toString());

        return new ResponseEntity<>(handleOrderPackageCounterResponse(counterResp,
                timeInfo.getMiddleTime(), languageContext.language()), HttpStatus.OK);
    }

    private CounterResponse handleOrderPackageCounterResponse(
            final CounterResponseWrapper<Counter> counterResp, final long middleTime,
            final String language) {
        if (counterResp == null) {
            return new CounterResponse();
        }

        CounterPrevCurrent orderCounter = null;
        CounterPrevCurrent sessionCounter = null;
        for (final Counter counter : counterResp.getCounters()) {
            if (ReportConstant.ORDER_COMPLETE_COUNTER.equalsIgnoreCase(counter.getName())) {
                orderCounter = calculatePrevCurrentData(counter, middleTime);
            } else if (ReportConstant.TOTAL_SESSION_COUNTER.equalsIgnoreCase(counter.getName())) {
                sessionCounter = calculatePrevCurrentData(counter, middleTime);
            }
        }

        if (orderCounter == null || sessionCounter == null) {
            return null;
        }
        return calculateAveragePackagePurchase(orderCounter, sessionCounter, language);
    }

    private CounterPrevCurrent calculatePrevCurrentData(final Counter counter, final long middleTime) {
        long current = 0;
        long prev = 0;
        for (final CounterValue value : counter.getValues()) {
            if (value.getTimestamp() < middleTime) {
                prev += value.getValue();
            }
            if (value.getTimestamp() >= middleTime) {
                current += value.getValue();
            }
        }

        return new CounterPrevCurrent(prev, current);
    }

    private CounterResponse calculateAveragePackagePurchase(final CounterPrevCurrent orderCounter,
                                                            final CounterPrevCurrent sessionCounter, final String language) {
        double prevAvg = 0.0;
        double curAvg = 0.0;
        int trend = 0;
        if (orderCounter.getPrev() > 0 && sessionCounter.getPrev() > 0) {
            prevAvg = Math.round(100.0 * sessionCounter.getPrev() / orderCounter.getPrev()) / 100.0;
        }

        if (orderCounter.getCurrent() > 0 && sessionCounter.getCurrent() > 0) {
            curAvg = Math.round(100.0 * sessionCounter.getCurrent() / orderCounter.getCurrent()) / 100.0;
        }

        if (prevAvg > 0.0 && curAvg > 0.0) {
            trend = (int) Math.round((curAvg - prevAvg) / prevAvg * 100);
        }

        final CounterResponse response = new CounterResponse();
        response.setName(ReportConstant.AVG_PACKAGE_PURCHAGE);
        response.setTotal(NumberUtils.getFormatNumber(curAvg, language));
        response.setTrend(trend);

        return response;
    }

    private TimeInfo calculateTimeRange(final TimeRange timeRange) {
        long startTime = 0;
        long endTime = 0;
        long middleTime = 0;

        switch (timeRange) {
            case WEEK:
                startTime = DateTimeConvertUtils.getStartTimeOfDate(ReportConstant.LAST_TWO_WEEKS);
                middleTime = DateTimeConvertUtils.getStartTimeOfDate(ReportConstant.LAST_WEEK);
                endTime = DateTimeConvertUtils.getEndTimeOfDate(ReportConstant.YESTERDAY);
                break;
            case MONTH:
                startTime = DateTimeConvertUtils.getStartTimeOfDate(ReportConstant.LAST_TWO_MONTHS);
                middleTime = DateTimeConvertUtils.getStartTimeOfDate(ReportConstant.LAST_MONTH);
                endTime = DateTimeConvertUtils.getEndTimeOfDate(ReportConstant.YESTERDAY);
                break;
            default:
                break;
        }

        if (startTime == 0 || endTime == 0 || middleTime == 0) {
            throw new InvalidTimeRangeException(String.format("Unsupport time range %s", timeRange));
        }

        LOG.info("Start time {}, middle time {}, end time {}", startTime, middleTime, endTime);
        return new TimeInfo(startTime, middleTime, endTime);
    }

    private List<CounterResponse> processCounterResponse(
            final CounterResponseWrapper<Counter> response, final long middleTime) {

        if (response == null) {
            throw new UserGridException("Statistic error", "Error when get statistic");
        }

        final List<CounterResponse> result = new ArrayList<>();
        for (final Counter counter : response.getCounters()) {
            result.add(processCounterData(counter, middleTime));
        }

        return result;
    }

    private CounterResponse processCounterData(final Counter counter, final long middleTime) {
        final CounterResponse response = new CounterResponse();
        response.setName(ReportUtils.resolveSessionStatus(counter.getName()));

        if (counter.getValues().isEmpty()) {
            counter.setValues(Collections.emptyList());
            return response;
        }

        long countPrevious = 0;
        long countCurrent = 0;
        for (final CounterValue value : counter.getValues()) {
            if (value.getTimestamp() < middleTime) {
                countPrevious += value.getValue();
            }

            if (value.getTimestamp() >= middleTime) {
                countCurrent += value.getValue();
            }
        }

        LOG.info("Counter {} statistic - Previous {} - Current {}", response.getName(),
                countPrevious, countCurrent);

        response.setTotal(String.valueOf(countCurrent));

        if (countPrevious > 0 && countCurrent > 0) {
            response.setTrend((int) Math.round((double) (countCurrent - countPrevious)
                    / countPrevious * 100));
        }

        return response;
    }

    class CounterPrevCurrent {
        private final long prev;
        private final long current;

        public CounterPrevCurrent(final long prev, final long current) {
            super();
            this.prev = prev;
            this.current = current;
        }

        public long getPrev() {
            return prev;
        }

        public long getCurrent() {
            return current;
        }

    }
}
