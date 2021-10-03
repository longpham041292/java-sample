package asia.cmg.f8.session.service;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.dto.EventTimeRange;
import asia.cmg.f8.session.dto.ScheduleEventRequest;
import asia.cmg.f8.session.dto.ScheduleEventResponse;
import asia.cmg.f8.session.entity.ScheduleEvent;
import asia.cmg.f8.session.repository.AvailabilityRepository;
import asia.cmg.f8.session.repository.ScheduleEventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service handle all operation for Schedule Event.
 * 
 * @author tung.nguyenthanh
 *
 */
@Service
public class ScheduleEventService {

    @Autowired
    private ScheduleEventRepository scheduleEventRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Transactional
    public boolean createScheduleEvent(final ScheduleEventRequest scheduleEvent,
            final String userUuid, final boolean isPt) {
        if (!validateTimeRange(scheduleEvent)) {
            throw new IllegalArgumentException("Time range is invalid");
        }
        final String title = scheduleEvent.getTitle();
        final boolean availableToTrain = isPt ? scheduleEvent.isAvailableToTrain()
                : Boolean.FALSE;

        final List<ScheduleEvent> scheduleEventList = scheduleEvent.getSchedule().stream()
                .map(event -> createEvent(event, userUuid, title, availableToTrain))
                .collect(Collectors.toList());

        scheduleEventRepository.save(scheduleEventList);

        if (isPt) {
            scheduleEventList.stream().forEach(
                    event -> {
                        availabilityRepository.deleteExistingAvailability(event.getStartedTime(),
                                event.getEndedTime(), userUuid);
                        if (availableToTrain) {
                            availabilityService.setupAvailablityForTrainer(userUuid,
                                    event.getStartedTime(), event.getEndedTime());
                        }
                    });
        }

        return Boolean.TRUE;
    }

    @Transactional
    public boolean updateScheduleEvent(final ScheduleEventRequest scheduleEvent,
            final String eventUuid, final String userUuid, final boolean isPt) {
        validateUpdateData(scheduleEvent);

        final Optional<ScheduleEvent> scheduleEventOpt = scheduleEventRepository
                .findOneByUserAndUuid(eventUuid, userUuid);
        if (!scheduleEventOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid event uuid");
        }

        final ScheduleEvent event = scheduleEventOpt.get();
        event.setTitle(scheduleEvent.getTitle());

        boolean needToUpdateAvailability = false;
        final boolean availableToTrain = isPt ? scheduleEvent.isAvailableToTrain()
                : Boolean.FALSE;
        final EventTimeRange newTimeRange = scheduleEvent.getSchedule().iterator().next();
        final LocalDateTime startedTime = ZoneDateTimeUtils
                .convertFromUTCToLocalDateTime(newTimeRange.getStartTime());
        final LocalDateTime endedTime = ZoneDateTimeUtils
                .convertFromUTCToLocalDateTime(newTimeRange.getEndTime());

        if (!isPt) {
            event.setStartedTime(startedTime);
            event.setEndedTime(endedTime);
            scheduleEventRepository.saveAndFlush(event);
            return Boolean.TRUE;
        }

        if (event.isAvailableToTrain() != availableToTrain) {
            needToUpdateAvailability = true;
        }

        final LocalDateTime oldStartTime = event.getStartedTime();
        final LocalDateTime oldEndTime = event.getEndedTime();

        if (!oldStartTime.isEqual(startedTime) || !oldEndTime.isEqual(endedTime)) {
            needToUpdateAvailability = true;
        }

        event.setStartedTime(startedTime);
        event.setEndedTime(endedTime);
        event.setAvailableToTrain(availableToTrain);
        scheduleEventRepository.saveAndFlush(event);

        if (needToUpdateAvailability) {
            // we just need to update availability in new time range. Old time
            // range should be keep it as it was.
            // availabilityRepository.deleteExistingAvailability(oldStartTime,
            // oldEndTime, userUuid);
            availabilityRepository.deleteExistingAvailability(startedTime, endedTime, userUuid);
            if (availableToTrain) {
                availabilityService.setupAvailablityForTrainer(userUuid, startedTime, endedTime);
            }
        }

        return Boolean.TRUE;
    }

    @Transactional
    public boolean deleteScheduleEvent(final String uuid, final String ownerUuid) {
        return scheduleEventRepository.deleteEventByUuid(uuid, ownerUuid) == 1;
    }
    
    public List<ScheduleEventResponse> getScheduleEventByTimeRange(final String ownerUuid, LocalDateTime startTime, LocalDateTime endTime){
    	return scheduleEventRepository.findScheduleEventsInTimeRange(ownerUuid, startTime, endTime);
    }

    private void validateUpdateData(final ScheduleEventRequest scheduleEvent) {
        // Event entity has only 1 slot. So we only support update 1 time slot
        if (scheduleEvent.getSchedule().size() != 1) {
            throw new IllegalArgumentException(String.format(
                    "Number of timeslot is not correct. Expect 1, input %s", scheduleEvent
                            .getSchedule().size()));
        }
        if (!validateTimeRange(scheduleEvent)) {
            throw new IllegalArgumentException("Time range is invalid");
        }
    }

    private ScheduleEvent createEvent(final EventTimeRange timeRange, final String uuid,
            final String title, final boolean availableToTrain) {
        final ScheduleEvent event = new ScheduleEvent();
        event.setOwnerUuid(uuid);
        event.setTitle(title);
        event.setAvailableToTrain(availableToTrain);
        event.setUuid(UUID.randomUUID().toString());

        final LocalDateTime startedTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeRange
                .getStartTime());
        final LocalDateTime endedTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(timeRange
                .getEndTime());
        event.setStartedTime(startedTime);
        event.setEndedTime(endedTime);
        return event;
    }

    private boolean validateTimeRange(final ScheduleEventRequest scheduleEvent) {
        return scheduleEvent.getSchedule().stream().filter(event -> isInvalid(event)).findFirst()
                .isPresent();
    }

    private boolean isInvalid(final EventTimeRange event) {
        final LocalDateTime startTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(event
                .getStartTime());
        final LocalDateTime endTime = ZoneDateTimeUtils.convertFromUTCToLocalDateTime(event
                .getEndTime());
        return isRoundToHour(startTime) && isRoundToHour(endTime) && startTime.isBefore(endTime);
    }

    private boolean isRoundToHour(final LocalDateTime input) {
        return input.getMinute() % 30 == 0 && input.getSecond() == 0;
    }
}
