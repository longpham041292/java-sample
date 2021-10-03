package asia.cmg.f8.session.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.session.dto.ScheduleEventRequest;
import asia.cmg.f8.session.dto.ScheduleEventResponse;
import asia.cmg.f8.session.service.ScheduleEventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

/**
 * 
 * @author tung.nguyenthanh
 *
 */
@RestController
public class ScheduleEventApi {
    private static final String SUCCESS = "success";
    private static final String EVENT_UUID = "eventUuid";
    private static final String TRAINER_UUID = "trainerUuid";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String OWNER_UUID = "ownerUuid";

    @Autowired
    private ScheduleEventService scheduleEventService;

    @GetMapping(value = "/calendar/schedule/events/{ownerUuid}/", produces = APPLICATION_JSON_UTF8_VALUE)
    public List<ScheduleEventResponse> getScheduleEvents(@RequestParam(value = START_TIME) final Long startTime,
            											@RequestParam(value = END_TIME) final Long endTime,
            											@PathVariable(value = OWNER_UUID) final String ownerUuid,
            											final Account account) throws OperationNotSupportedException {
    	if (!isEuOrPt(account)) {
            throw new OperationNotSupportedException("This action is not allowed for current user");
        }
    	
    	final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
    	
    	return scheduleEventService.getScheduleEventByTimeRange(ownerUuid, start, end);
    }
    
    @RequestMapping(value = "/calendar/schedule/events/", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Boolean>> createScheduleEvents(
            @RequestBody final ScheduleEventRequest scheduleEvent, final Account account)
            throws OperationNotSupportedException {
        if (!isEuOrPt(account)) {
            throw new OperationNotSupportedException("This action is not allowed for current user");
        }
        return new ResponseEntity<Map<String, Boolean>>(Collections.singletonMap(SUCCESS,
                scheduleEventService.createScheduleEvent(scheduleEvent, account.uuid(),
                        account.isPt())), HttpStatus.OK);
    }

    @RequestMapping(value = "/calendar/schedule/events/{eventUuid}", method = RequestMethod.PUT,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Boolean>> updateScheduleEvents(@PathVariable(
            value = EVENT_UUID) final String eventUuid,
            @RequestBody final ScheduleEventRequest scheduleEvent, final Account account) {
        if (!isEuOrPt(account)) {
            throw new IllegalAccessError("This action is not allowed for current user");
        }
        return new ResponseEntity<Map<String, Boolean>>(Collections.singletonMap(SUCCESS,
                scheduleEventService.updateScheduleEvent(scheduleEvent, eventUuid, account.uuid(),
                        account.isPt())),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/calendar/schedule/events/{eventUuid}", method = RequestMethod.DELETE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, Boolean>> deleteScheduleEvents(@PathVariable(
            value = EVENT_UUID) final String eventUuid, final Account account) {
        if (!isEuOrPt(account)) {
            throw new IllegalAccessError("This action is not allowed for current user");
        }
        return new ResponseEntity<Map<String, Boolean>>(Collections.singletonMap(SUCCESS,
                scheduleEventService.deleteScheduleEvent(eventUuid, account.uuid())), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/calendar/trainers/{trainerUuid}/schedule/events/{eventUuid}",
            method = RequestMethod.PUT, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Map<String, Boolean>> adminUpdateScheduleEvents(@PathVariable(
            value = TRAINER_UUID) final String trainerUuid,
            @PathVariable(value = EVENT_UUID) final String eventUuid,
            @RequestBody final ScheduleEventRequest scheduleEvent, final Account account) {
        return new ResponseEntity<Map<String, Boolean>>(Collections.singletonMap(SUCCESS,
                scheduleEventService.updateScheduleEvent(scheduleEvent, eventUuid, trainerUuid,
                        true)), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/calendar/trainers/{trainerUuid}/schedule/events/{eventUuid}",
            method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Map<String, Boolean>> adminDeleteScheduleEvents(@PathVariable(
            value = TRAINER_UUID) final String trainerUuid,
            @PathVariable(value = EVENT_UUID) final String eventUuid, final Account account) {
        return new ResponseEntity<Map<String, Boolean>>(Collections.singletonMap(SUCCESS,
                scheduleEventService.deleteScheduleEvent(eventUuid, trainerUuid)), HttpStatus.OK);
    }

    private boolean isEuOrPt(final Account account) {
        return account.isEu() || account.isPt();
    }
}
