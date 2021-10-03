package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.dto.ScheduleEventResponse;
import asia.cmg.f8.session.entity.ScheduleEvent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {

    @Query("select se from ScheduleEvent se where se.uuid = ?1 and se.ownerUuid = ?2")
    Optional<ScheduleEvent> findOneByUserAndUuid(final String uuid, final String ownerUuid);

    @Modifying
    @Query("delete from ScheduleEvent se where se.uuid = ?1 and se.ownerUuid = ?2")
    int deleteEventByUuid(final String uuid, final String ownerUuid);

    @Query("select se from ScheduleEvent se where se.ownerUuid=?1 and se.startedTime >= ?2 "
            + "and se.endedTime < ?3 order by se.startedTime asc")
    List<ScheduleEvent> findEventsInTimeRange(final String ownerUuid,
            final LocalDateTime startTime,
            final LocalDateTime endTime);
    
    @Query("select new asia.cmg.f8.session.dto.ScheduleEventResponse(se) "
    		+ "from ScheduleEvent se "
    		+ "where se.ownerUuid=?1 and se.startedTime >= ?2 and se.endedTime < ?3 "
            + "order by se.startedTime asc")
    List<ScheduleEventResponse> findScheduleEventsInTimeRange(final String ownerUuid,
												            final LocalDateTime startTime,
												            final LocalDateTime endTime);
}
