package asia.cmg.f8.session.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.session.entity.BasicUserEntity;
import asia.cmg.f8.session.entity.WhosHotEntity;


public interface WhosHotRepository extends JpaRepository<WhosHotEntity, Long>{

    @Query("select w from WhosHotEntity w  where w.ptUuid = ?1")
    List<WhosHotEntity> findByPTUuid(final String ptUuid);

    @Query("select e, (?1 *  CASE WHEN (likes_number >= ?2) THEN likes_number ELSE 0 END  + " +
            "           ?3 *  CASE WHEN (posts_number >= ?4) THEN posts_number ELSE 0 END + " +
            "           ?5 *  CASE WHEN (clients_number >= ?6) THEN clients_number ELSE 0 END + " +
            "           ?7 *  CASE WHEN (sessions_burned_number >= ?8) THEN sessions_burned_number ELSE 0 END ) " +
            "            as totalPoint " +
            "from WhosHotEntity w join BasicUserEntity e on w.ptUuid = e.uuid " +
            "where e.activated=true " + 
            "and (w.ptUuid in ?9 or e.clubcode = ?10) " +
            "order by totalPoint DESC, e.fullName ASC ")
    List<BasicUserEntity> getTopWhosHotUsers(final int likesWeight,
                                             final int likesMinRequired,
                                             final int postsWeight,
                                             final int postsMinRequired,
                                             final int clientsWeight,
                                             final int clientsMinRequired,
                                             final int sessionsBurnedWeight,
                                             final int sessionsBurnedMinRequired,
                                             final List<String> uuids,
                                             final String nearestClubCode,
                                             final Pageable topItems
                                            );

    @Query("select e, (?1 *  CASE WHEN (likes_number >= ?2) THEN likes_number ELSE 0 END  + " +
            "           ?3 *  CASE WHEN (posts_number >= ?4) THEN posts_number ELSE 0 END + " +
            "           ?5 *  CASE WHEN (clients_number >= ?6) THEN clients_number ELSE 0 END + " +
            "           ?7 *  CASE WHEN (sessions_burned_number >= ?8) THEN sessions_burned_number ELSE 0 END ) " +
            "            as totalPoint " +
            "from WhosHotEntity w join BasicUserEntity e on w.ptUuid = e.uuid " +
            "where e.clubcode = ?9 AND e.activated=true " + 
            "order by totalPoint DESC, e.fullName ASC ")
    List<BasicUserEntity> getTopWhosHotUsers(final int likesWeight,
                                             final int likesMinRequired,
                                             final int postsWeight,
                                             final int postsMinRequired,
                                             final int clientsWeight,
                                             final int clientsMinRequired,
                                             final int sessionsBurnedWeight,
                                             final int sessionsBurnedMinRequired,
                                             final String nearestClubCode,
                                             final Pageable topItems
                                            );
}
