package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.web.errorcode.ErrorCode;
import asia.cmg.f8.session.dto.ClubDto;
import asia.cmg.f8.session.entity.ClubEntity;
import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.repository.EventRepository;
import asia.cmg.f8.session.service.EventManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static asia.cmg.f8.session.entity.SessionStatus.OPEN;

/**
 * Created on 12/22/16.
 */
@Service
public class InternalEventManagementService implements EventManagementService {

    private final EventRepository eventRepository;
    private final Logger LOG = LoggerFactory.getLogger(InternalEventManagementService.class);

    @Autowired
    public InternalEventManagementService(final EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public EventEntity create(final String ownerId, final LocalDateTime startTime,
                              final LocalDateTime endTime, final String sessionUuid,
                              final SessionStatus status,
                              final String bookedBy,
                              final ClubDto club) {
        final EventEntity newEvent = new EventEntity();
        newEvent.setUuid(UUID.randomUUID().toString());
        newEvent.setStartTime(startTime);
        newEvent.setEndTime(endTime);
        newEvent.setSessionUuid(sessionUuid);
        newEvent.setOwnerId(ownerId);
        newEvent.setStatus(status);
        newEvent.setBookedBy(bookedBy);
        newEvent.setClubUuid(club.getUuid());
        newEvent.setClubName(club.getName());
        newEvent.setClubAddress(club.getAddress());
        return eventRepository.save(newEvent);
    }

    @Override
    public void expireEvents(final List<String> listEventUuid) {
        eventRepository.batchUpdateEventStatus(listEventUuid, SessionStatus.EXPIRED);
    }

    @Override
    @Transactional
    public void deleteEvents(final List<String> listEventUuid) {
        eventRepository.batchDeleteEvents(listEventUuid);
    }

    @Override
    @Transactional
    public void batchDeleteEventsWithUuid(final String sessionUuid, final SessionStatus status, final List<String> eventUuidList) {
        eventRepository.batchDeleteEventsWithUuid(sessionUuid, status, eventUuidList);
    }

    @Override
    public void updateEventStatus(final SessionEntity sessionEntity) {
        final String userEventId = sessionEntity.getUserEventId();
        final String ptEventId = sessionEntity.getPtEventId();
        final SessionStatus currentStatus = sessionEntity.getStatus();

        if (OPEN.equals(currentStatus)) {
            throw new IllegalStateException("Does not allow to update an event with OPEN status");
        }
        eventRepository.updateStatus(currentStatus, sessionEntity.getUuid(), Arrays.asList(userEventId, ptEventId));
    }
    
    @Override
    public ErrorCode updateEventWithCheckinClub(final SessionEntity sessionEntity, ClubEntity checkinClub) {
    	try {
    		final String userEventId = sessionEntity.getUserEventId();
            final String ptEventId = sessionEntity.getPtEventId();
            final SessionStatus currentStatus = sessionEntity.getStatus();

            if (OPEN.equals(currentStatus)) {
                throw new IllegalStateException("Does not allow to update an event with OPEN status");
            }
            eventRepository.updateSessionEventWithCheckinClub(checkinClub.getId(), 
            												checkinClub.getName(), 
            												checkinClub.getAddress(), 
            												sessionEntity.getStatus().name(), 
            												sessionEntity.getUuid(),
            												Arrays.asList(userEventId, ptEventId));
            
            return ErrorCode.SUCCESS;
		} catch (Exception e) {
			LOG.error("[DB ERR][updateEventWithCheckinClub][error: {}]", e.getMessage());
			return ErrorCode.DB_ERROR_UPDATE.withDetail(String.format("Update session event{session_uuid=%s} failed", sessionEntity.getUuid()));
		}
    }

	@Override
	public EventEntity getEventByUuid(String uuid) {
		try {
			Optional<EventEntity> optEventEntity = eventRepository.getByUuid(uuid);
			return (optEventEntity.isPresent() == true ? optEventEntity.get() : null);
		} catch (Exception e) {
			LOG.error("[DB ERR][getEventByUuid][uuid: {}][error: {}]", uuid, e.getMessage());
			return null;
		}
	}
}
