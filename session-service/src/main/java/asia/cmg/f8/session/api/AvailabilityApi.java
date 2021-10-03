package asia.cmg.f8.session.api;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredPTRole;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.AvailabilitiesPreferTimeRequest;
import asia.cmg.f8.session.dto.Availability;
import asia.cmg.f8.session.dto.AvailabilityRequest;
import asia.cmg.f8.session.entity.AvailabilityEntity;
import asia.cmg.f8.session.service.AvailabilityService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 11/22/16.
 */
@RestController
public class AvailabilityApi {

    private final AvailabilityService availabilityService;

    public AvailabilityApi(final AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @RequiredPTRole
    @RequestMapping(
            value = "/availabilities", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public List<Availability> ptGetAvailability(
            @RequestParam final int month,
            @RequestParam final int year,
            final Account account) {
        return getAvailabilityOfUser(month, year, account.uuid());
    }

    @RequiredPTRole
    @RequestMapping(
            value = "/availabilities", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> setAvailabilityByUser(
            @RequestBody @Valid final AvailabilityRequest request,
            final Account account) {
        return Collections.singletonMap("success",
                availabilityService.setAvailabilityByUser(request, account.uuid()));
    }

    @RequestMapping(
            value = "/users/me/availabilities/trainers/{trainerId}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> getAvailabilityWithPreferTimeByTrainer(@ModelAttribute final AvailabilitiesPreferTimeRequest preferTime,
                                                                      @RequestParam(value = "start_date",required = false) final Long startDate,
                                                                      @RequestParam(value = "end_date",required = false) final Long endDate,
                                                                      @RequestParam("start_time") final Long startTime,
                                                                      @RequestParam("end_time") final Long endTime,
                                                                      @PathVariable("trainerId") final String trainerId) {
        preferTime.setStartDate(startDate);
        preferTime.setEndDate(endDate);
        preferTime.setStartTime(startTime);
        preferTime.setEndTime(endTime);

        return Collections.singletonMap("availabilities",
                availabilityService.getAvailabilityWithPreferTimeByTrainer(preferTime, trainerId));
    }

    private List<Availability> getAvailabilityOfUser(final int month,
                                                     final int year,
                                                     final String userId) {

        final List<AvailabilityEntity> availabilities =
                availabilityService.getAvailabilityByUser(month, year, userId);

        return availabilities.stream().map(entity -> Availability.builder()
                .startDate(ZoneDateTimeUtils.convertToSecondUTC(entity.getStartedTime()))
                .endDate(ZoneDateTimeUtils.convertToSecondUTC(entity.getEndedTime()))
                .build()).collect(Collectors.toList());
    }
}
