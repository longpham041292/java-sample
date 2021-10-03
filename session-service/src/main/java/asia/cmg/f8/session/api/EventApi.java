package asia.cmg.f8.session.api;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.dto.EventsResponse;
import asia.cmg.f8.session.service.EventService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created on 12/24/16.
 */
@RestController
public class EventApi {

    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String TRAINER_ID = "trainerId";
    private static final String VERSION = "v";


    private final EventService eventService;

    @Inject
    public EventApi(final EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(
            value = "/sessions/users/me", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public List<EventsResponse> getSessionByUser(@RequestParam(value = START_TIME) final Long startTime,
                                                 @RequestParam(value = END_TIME) final Long endTime,
                                                 @RequestParam(value = VERSION, defaultValue="") final String version,
                                                 final Account account) {
        final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());

        return eventService.getAllEventByUser(account.uuid(), start, end,
                UserType.valueOf(account.type().toUpperCase(Locale.getDefault())), version);
    }

    @RequestMapping(
            value = "/admin/sessions/users/{trainerId}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public List<EventsResponse> getSessionByTrainerOnBehalf(@PathVariable(value = TRAINER_ID) final String userId,
                                                            @RequestParam(value = START_TIME) final Long startTime,
                                                            @RequestParam(value = END_TIME) final Long endTime,
                                                            @RequestParam(value = VERSION, defaultValue="") final String version) {
        final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
        final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());

        return eventService.getAllEventByUser(userId, start, end, UserType.PT, version);
    }
}
