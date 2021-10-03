package asia.cmg.f8.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.session.entity.EventEntity;
import asia.cmg.f8.session.entity.SessionStatus;

/**
 * Created on 11/22/16.
 */
public interface EventRepository extends CrudRepository<EventEntity, String> {

    @Query(value = "SELECT " +
            "  ee.ownerId AS user_id," +
            "  se.ptUuid as trainer_id," +
            "  ee.sessionUuid AS session_id," +
            "  ee.startTime AS event_start_date," +
            "  ee.endTime AS event_end_date," +
            "  ee.status AS session_status," +
            "  su.avatar AS user_avatar," +
            "  su.email AS user_email," +
            "  su.fullName AS user_name," +
            "  st.avatar AS trainer_avatar," +
            "  st.email AS trainer_email," +
            "  st.fullName AS trainer_name," +
            "  st.phone AS trainer_phone," +
            "  su.phone AS user_phone, " +
            "  sp.numOfBurned AS session_burned, " +
            "  sp.numOfSessions AS session_number, " +
            "  ee.uuid AS event_id, " +
            "  ee.bookedBy AS booked_by, " +
            "  se.status AS status, " +
            "  o.expiredDate AS expiredDate, " +
            "  ee.clubUuid, " +
            "  ee.clubName, " +
            "  ee.clubAddress, " +
            "  ee.checkinClubUuid, " +
            "  ee.checkinClubName, " +
            "  ee.checkinClubAddress " +
            " FROM EventEntity ee " +
            "   JOIN SessionEntity se ON ee.sessionUuid = se.uuid " +
            "   JOIN SessionPackageEntity sp ON se.packageUuid = sp.uuid " +
            "   LEFT JOIN BasicUserEntity su ON ee.ownerId = su.uuid " +
            "   LEFT JOIN BasicUserEntity st ON se.ptUuid = st.uuid " +
            "   LEFT JOIN OrderEntity o ON o.uuid = sp.orderUuid " +
            " WHERE ee.ownerId = :ownerId AND" +
            "      ee.startTime > :startTime AND" +
            "      ee.endTime < :endTime " +
            " ORDER BY ee.startTime ASC ")
    List<Object[]> getAllEventByEndUser(@Param("ownerId") final String ownerId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    @Query(value = "SELECT " +
            "  sp.userUuid AS user_id," +
            "  sp.ptUuid as trainer_id," +
            "  ee.sessionUuid AS session_id," +
            "  ee.startTime AS event_start_date," +
            "  ee.endTime AS event_end_date," +
            "  ee.status AS session_status," +
            "  su.avatar AS user_avatar," +
            "  su.email AS user_email," +
            "  su.fullName AS user_name," +
            "  st.avatar AS trainer_avatar," +
            "  st.email AS trainer_email," +
            "  st.fullName AS trainer_name," +
            "  st.phone AS trainer_phone," +
            "  su.phone AS user_phone, " +
            "  sp.numOfBurned AS session_burned, " +
            "  sp.numOfSessions AS session_number, " +
            "  ee.uuid AS event_id, " +
            "  ee.bookedBy AS booked_by, " +
            "  se.status AS status, " +
            "  o.expiredDate AS expiredDate, " +
            "  ee.clubUuid, " +
            "  ee.clubName, " +
            "  ee.clubAddress, " +
            "  ee.checkinClubUuid, " +
            "  ee.checkinClubName, " +
            "  ee.checkinClubAddress " +
            " FROM EventEntity ee JOIN SessionEntity se ON ee.sessionUuid = se.uuid " +
            "   JOIN SessionPackageEntity sp ON se.packageUuid = sp.uuid" +
            "   LEFT JOIN BasicUserEntity st ON ee.ownerId = st.uuid " +
            "   LEFT JOIN BasicUserEntity su ON sp.userUuid = su.uuid " +
            "   LEFT JOIN OrderEntity o ON o.uuid = sp.orderUuid " +
            " WHERE ee.ownerId = :ownerId AND" +
            "      ee.startTime > :startTime AND" +
            "      ee.endTime < :endTime " +
            " ORDER BY ee.startTime ASC ")
    List<Object[]> getAllEventByTrainer(@Param("ownerId") final String ownerId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    @Modifying
    @Query(value = "UPDATE EventEntity ee SET ee.status = :newStatus " +
            "WHERE ee.sessionUuid = :sessionUuid AND ee.status = :oldStatus ")
    void updateNewStatusByOldStatus(@Param("sessionUuid") final String sessionUuid,
                                    @Param("oldStatus") final SessionStatus oldStatus,
                                    @Param("newStatus") final SessionStatus newStatus);


    @Modifying
    @Query(value = "UPDATE EventEntity ee SET ee.status = :newStatus WHERE ee.uuid in :eventUuidList")
    void batchUpdateEventStatus(@Param("eventUuidList") final List<String> eventUuidList,
                                @Param("newStatus") final SessionStatus newStatus);

    @Modifying
    @Query("DELETE FROM EventEntity ee WHERE ee.uuid in :eventUuidList")
    void batchDeleteEvents(@Param("eventUuidList") final List<String> eventUuidList);
    

    @Modifying
    @Query("DELETE FROM EventEntity ee WHERE ee.sessionUuid = :sessionUuid and ee.status = :status and ee.uuid not in :eventUuidList")
    void batchDeleteEventsWithUuid(@Param("sessionUuid") final String sessionUuid,  @Param("status") final SessionStatus status, @Param("eventUuidList") final List<String> eventUuidList);

    @Modifying
    @Query(value = "update EventEntity event set event.status = :status where event.sessionUuid = :sessionUuid and event.uuid in :eventUuids")
    void updateStatus(@Param(("status")) SessionStatus currentStatus, @Param("sessionUuid") String sessionUuid, @Param("eventUuids") List<String> strings);
    
    @Modifying
    @Query("UPDATE FROM EventEntity ee SET ee.startTime = :startTime, ee.endTime= :endTime WHERE ee.uuid= :uuid")
    void updateSessionEventTime(@Param("uuid") final String uuid, @Param("startTime") final LocalDateTime startTime, @Param("endTime") final LocalDateTime endTime);
    
    @Query(value = "SELECT * FROM session_events WHERE uuid = ?1", nativeQuery = true)
    Optional<EventEntity> getByUuid(final String uuid);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE session_events SET "
    		+ "checkin_club_uuid = ?1, "
    		+ "checkin_club_name = ?2, "
    		+ "checkin_club_address = ?3, "
    		+ "status = ?4 "
    		+ "WHERE session_uuid = ?5 and uuid in ?6", nativeQuery = true)
    void updateSessionEventWithCheckinClub(final String clubUuid, final String clubName, final String clubAddress, final String status, final String sessionUuid, final List<String> strings);
}
