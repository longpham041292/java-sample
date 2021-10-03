package asia.cmg.f8.session.service;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.utils.SessionErrorCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 12/22/16.
 */
public interface EventManagementService {

    /**
     * Create a new Event
     *
     * @param ownerId     the event owner uuid
     * @param startTime   the event start time
     * @param endTime     the event end time
     * @param sessionUuid the session uuid
     * @return the new {@link EventEntity}
     */
    EventEntity create(final String ownerId,
                       final LocalDateTime startTime,
                       final LocalDateTime endTime,
                       final String sessionUuid,
                       final SessionStatus status,
                       final String bookedBy,
                       final ClubDto club);

    /**
     * Set list of given event uuids as expired.
     *
     * @param listEventUuid List of event's uuid
     */
    void expireEvents(final List<String> listEventUuid);

    /**
     * Delete a list of events
     *
     * @param listEventUuid List of event's uuid
     */
    void deleteEvents(List<String> listEventUuid);

    /**
     * Delete a list of events
     *
     * @param sessionUuid event's uuid
     * @param status status of session
     * @param eventUuidList list current event
     */
    void batchDeleteEventsWithUuid(final String sessionUuid, final SessionStatus status, final List<String> eventUuidList);

    /**
     * Update status of {@link EventEntity} which is booked for this session
     *
     * @param sessionEntity the session entity
     */
    void updateEventStatus(SessionEntity sessionEntity);
    
    EventEntity getEventByUuid(String uuid);
    
    ErrorCode updateEventWithCheckinClub(final SessionEntity sessionEntity, ClubEntity checkinClub);
}
