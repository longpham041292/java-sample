package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.AvailabilityEntity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 11/22/16.
 */
public interface AvailabilityRepository extends CrudRepository<AvailabilityEntity, String> {

    @Query("select a from AvailabilityEntity a where a.startedTime >= ?1 and a.endedTime <= ?2 and a.userId = ?3 order by a.startedTime asc")
    List<AvailabilityEntity> findAllByUserAtGivenTimeRange(final LocalDateTime fromDate, final LocalDateTime toDate, final String userUuid);

    @Modifying(clearAutomatically = true)
    @Query("delete from AvailabilityEntity a where a.startedTime >= ?1 and a.endedTime <= ?2 and a.userId = ?3")
    void deleteExistingAvailability(final LocalDateTime fromDate, final LocalDateTime toDate, final String userUuid);

    /**
     * 
     * @param toDate
     *            MUST ALWAYS in the PAST.
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query("delete from AvailabilityEntity ae where ae.startedTime <= ?1")
    int deleteAllOldAvailability(final LocalDateTime toDate);
}
