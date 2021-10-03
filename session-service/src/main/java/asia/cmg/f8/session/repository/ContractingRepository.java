package asia.cmg.f8.session.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.session.entity.OrderEntity;

/**
 * Created on 12/7/16.
 */
public interface ContractingRepository extends CrudRepository<OrderEntity, String> {
    final String AND_CLIENT_ACTIVE_PACKAGE_QUERY = "and (so.expired_date IS NULL or (so.num_of_burned < so.num_of_sessions and so.expired_date >= CURRENT_DATE)) ";
    final String COUNT_NUM_OF_CONFIRMED = ",(SELECT count(ss.uuid) FROM session_sessions ss WHERE ss.package_uuid = sp.uuid AND ss.pt_uuid = sp.pt_uuid AND ss.user_uuid = sp.user_uuid AND ss.status = 'CONFIRMED') AS num_of_confirmed ";

    /**
     * @param trainerId current PT
     * @param keyword
     * @return List of Contracting EU of current PT
     */
    @Query(
            value = "SELECT (eu.user_uuid), eu.order_code, eu.full_name, eu.avatar, eu.username, eu.num_of_burned, eu.num_of_sessions, eu.expired_date, eu.order_date, eu.num_of_confirmed, eu.package_uuid, eu.level, eu.training_style "
                    + "FROM (SELECT sp.user_uuid, so.order_code, su.full_name, su.avatar, su.username, su.level, sp.num_of_burned, sp.num_of_sessions, so.expired_date, so.order_date, sp.uuid as package_uuid, so.training_style " + COUNT_NUM_OF_CONFIRMED 
                    + "FROM session_orders so JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN (?2) "
                    + "LEFT JOIN session_users su ON so.user_uuid = su.uuid "
                    + "WHERE sp.pt_uuid = ?1 AND su.activated = true "
                    + AND_CLIENT_ACTIVE_PACKAGE_QUERY
                    + "AND (su.full_name LIKE concat('%',?3,'%') OR su.username LIKE concat('%',?3,'%')) "
                    + "ORDER BY so.expired_date asc) eu "
                    + "GROUP BY eu.user_uuid "
                    + "ORDER BY eu.expired_date IS NULL , eu.expired_date  ASC , eu.full_name ASC ", nativeQuery = true)
    List<Object[]> searchContractingEuUserByKeyword(
            final String trainerId,
            final List<String> validPackageStatus,
            final String keyword);
   
    /**
     * @param trainerId current PT
     * @param keyword
     * @return List of All Contracting EU of current PT
     */
    @Query(
            value = "SELECT eu.user_uuid, eu.order_code, eu.full_name, eu.avatar, eu.username, eu.num_of_burned, eu.num_of_sessions, eu.expired_date, eu.order_date, eu.num_of_confirmed, eu.package_uuid, eu.level, eu.training_style "
                    + "FROM (SELECT sp.user_uuid, sp.pt_uuid, so.order_code, su.full_name, su.level, su.avatar, su.username, sp.num_of_burned, sp.num_of_sessions, so.expired_date, so.order_date, sp.uuid as package_uuid, so.training_style " + COUNT_NUM_OF_CONFIRMED 
                    + "FROM session_orders so JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN (?2) "
                    + "LEFT JOIN session_users su ON so.user_uuid = su.uuid "
                    + "WHERE sp.pt_uuid = ?1 AND su.activated = true "
                    + "and (so.num_of_burned < so.num_of_sessions) "
                    + "AND (su.full_name LIKE concat('%',?3,'%') OR su.username LIKE concat('%',?3,'%')) ) eu "
                    + "GROUP BY eu.package_uuid "
                    + "ORDER BY eu.expired_date IS NULL, eu.expired_date  ASC , eu.full_name ASC ", nativeQuery = true)
    List<Object[]> searchAllContractingEuUserByKeyword(
            final String trainerId,
            final List<String> validPackageStatus,
            final String keyword);    

    /**
     * @param euId    current EU
     * @param keyword
     * @return List of Contracting PT of current EU
     */
    @Query(
            value = "SELECT (pt_uuid), pt.order_code, pt.full_name, pt.avatar, pt.username, pt.num_of_burned, pt.num_of_sessions, pt.expired_date, pt.order_date, pt.num_of_confirmed, pt.package_uuid, pt.level, pt.training_style "
                    + "FROM (SELECT sp.pt_uuid, so.order_code, su.full_name, su.level, su.avatar, su.username, sp.num_of_burned, sp.num_of_sessions, so.expired_date, so.order_date, sp.uuid as package_uuid, so.training_style " + COUNT_NUM_OF_CONFIRMED
                    + "FROM session_orders so JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN (?2) "
                    + "LEFT JOIN session_users su ON sp.pt_uuid = su.uuid "
                    + "WHERE sp.user_uuid = ?1 AND su.activated = true "
                    + AND_CLIENT_ACTIVE_PACKAGE_QUERY
                    + "AND (su.full_name LIKE concat('%',?3,'%') or su.username LIKE concat('%',?3,'%')) "
                    + "ORDER BY if(so.expired_date is null, 9999999999, UNIX_TIMESTAMP(so.expired_date)), so.number_of_limit_day, so.created_date, sp.created_date desc) pt "
                    + "GROUP BY pt.pt_uuid  "
                    + "ORDER BY pt.expired_date IS NULL , pt.expired_date  ASC , pt.full_name ASC ", nativeQuery = true)
    List<Object[]> searchContractingPtByKeyword(
            final String euId,
            final List<String> validPackageStatus,
            final String keyword);
    
    /**
     * @param euId    current EU
     * @param keyword
     * @return List of Contracting PT of current EU
     */
    @Query(
            value = "SELECT pt_uuid, pt.order_code, pt.full_name, pt.avatar, pt.username, pt.num_of_burned, pt.num_of_sessions, pt.expired_date, pt.order_date, pt.num_of_confirmed, pt.package_uuid, pt.level as pt_level, pt.training_style "
                    + "FROM (SELECT sp.pt_uuid, so.order_code, su.full_name, su.avatar, su.username, su.level, sp.num_of_burned, sp.num_of_sessions, so.expired_date, so.order_date, sp.uuid as package_uuid, so.training_style " + COUNT_NUM_OF_CONFIRMED
                    + "FROM session_orders so JOIN session_session_packages sp ON sp.order_uuid = so.uuid AND sp.status IN (?2) "
                    + "LEFT JOIN session_users su ON sp.pt_uuid = su.uuid "
                    + "WHERE sp.user_uuid = ?1 AND su.activated = true "
                    + "AND (so.num_of_burned < so.num_of_sessions) "
                    + "AND (su.full_name LIKE concat('%',?3,'%') or su.username LIKE concat('%',?3,'%')) ) pt "
                    + "GROUP BY pt.package_uuid  "
                    + "ORDER BY pt.expired_date IS NULL , pt.expired_date  ASC , pt.full_name ASC ", nativeQuery = true)
    List<Object[]> searchAllContractingPtByKeyword(
            final String euId,
            final List<String> validPackageStatus,
            final String keyword);    

    /**
     * @param euId current EU
     * @param validPackageStatus valid package statuses
     * @param keyword search keyword, searched by name or username
     * @param pageable pagination information
     * @return list of PTs excluding contracting PTs
     */
    @Query(
            value = "SELECT u.uuid, u.full_name, u.avatar, u.username, (SELECT EXISTS (SELECT 1 " +
																		"FROM following " +
																		"WHERE followed_user_id=u.uuid AND user_id=?1)) AS followed, level " +
                    "FROM session_users u " +
                    "WHERE u.activated=true AND u.user_type='PT' AND u.doc_status='APPROVED' AND (u.full_name LIKE concat('%',?3,'%') OR u.username LIKE concat('%',?3,'%')) " +
                    "AND u.uuid NOT IN (SELECT sp.pt_uuid FROM session_session_packages sp JOIN session_orders so ON sp.order_uuid = so.uuid " +
                    "WHERE sp.user_uuid=?1 AND sp.status IN (?2) " + AND_CLIENT_ACTIVE_PACKAGE_QUERY + ") " +
                    "\n#pageable\n ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM session_users " +
                    "WHERE activated=true AND user_type='PT' AND (full_name LIKE concat('%',?3,'%') OR username LIKE concat('%',?3,'%')) " +
                    "AND uuid NOT IN (SELECT pt_uuid FROM session_session_packages WHERE user_uuid=?1 AND status IN (?2))",
            nativeQuery = true)
    Page<Object[]> searchPTsExcludingContracting(
            final String euId,
            final List<String> validPackageStatus,
            final String keyword,
            final Pageable pageable
    );
    
    @Query(
            value = "SELECT u.uuid, u.full_name, u.avatar, u.username, (SELECT EXISTS (SELECT 1 " +
																		"FROM following " +
																		"WHERE followed_user_id=u.uuid AND user_id=?1)) AS followed, u.level " +
                    "FROM session_users u " +
                    "WHERE u.level = ?4 AND u.activated=true AND u.user_type='PT' AND u.doc_status='APPROVED' AND (u.full_name LIKE concat('%',?3,'%') OR u.username LIKE concat('%',?3,'%')) " +
                    "AND u.uuid NOT IN (SELECT sp.pt_uuid FROM session_session_packages sp JOIN session_orders so ON sp.order_uuid = so.uuid " +
                    "WHERE sp.user_uuid=?1 AND sp.status IN (?2) " + AND_CLIENT_ACTIVE_PACKAGE_QUERY + ") " +
                    "\n#pageable\n ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM session_users " +
                    "WHERE level = ?4 AND activated=true AND user_type='PT' AND (full_name LIKE concat('%',?3,'%') OR username LIKE concat('%',?3,'%')) " +
                    "AND uuid NOT IN (SELECT pt_uuid FROM session_session_packages WHERE user_uuid=?1 AND status IN (?2))",
            nativeQuery = true)
    Page<Object[]> searchPTsExcludingContractingAndSameLevel(
										            final String euId,
										            final List<String> validPackageStatus,
										            final String keyword,
										            final String userLevel,
										            final Pageable pageable);

    /**
     * @param euId current EU
     * @param validPackageStatus valid package statuses
     * @param keyword search keyword, searched by name or username
     * @param pageable pagination information
     * @return list of PTs excluding contracting PTs
     */
    @Query(
            value = "SELECT u.uuid, u.full_name, u.avatar, u.username, (SELECT EXISTS (SELECT 1 " +
																		"FROM following " +
																		"WHERE followed_user_id=u.uuid AND user_id=:userId )) AS followed, level " +
                    "FROM session_users u " +
                    "WHERE u.activated=true AND u.doc_status='APPROVED' AND u.user_type='PT' AND u.uuid NOT LIKE :userId AND (u.full_name LIKE concat('%',:keyword,'%') OR u.username LIKE concat('%',:keyword,'%')) " +
                    "\n#pageable\n ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM session_users " +
                    "WHERE activated=true AND doc_status='APPROVED' AND user_type='PT' AND uuid NOT LIKE :userId AND (full_name LIKE concat('%',:keyword,'%') OR username LIKE concat('%',:keyword,'%')) ",
                    nativeQuery = true)
    Page<Object[]> searchAllActivePTs(
    		@Param("keyword") final String keyword,
    		@Param("userId") final String userId,
            final Pageable pageable
        );
    
    @Query(
            value = "SELECT u.uuid, u.full_name, u.avatar, u.username, (SELECT EXISTS (SELECT 1 " +
																		"FROM following " +
																		"WHERE followed_user_id=u.uuid AND user_id=:userId )) AS followed, u.level " +
                    "FROM session_users u " +
                    "WHERE u.level = :userLevel AND u.activated=true AND u.doc_status='APPROVED' AND u.user_type='PT' AND u.uuid NOT LIKE :userId AND (u.full_name LIKE concat('%',:keyword,'%') OR u.username LIKE concat('%',:keyword,'%')) " +
                    "\n#pageable\n ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM session_users " +
                    "WHERE level = :userLevel AND activated=true AND doc_status='APPROVED' AND user_type='PT' AND uuid NOT LIKE :userId AND (full_name LIKE concat('%',:keyword,'%') OR username LIKE concat('%',:keyword,'%')) ",
                    nativeQuery = true)
    Page<Object[]> searchAllActivePTsSameLevel(
									    		@Param("keyword") final String keyword,
									    		@Param("userId") final String userId,
									    		@Param("userLevel") final String userLevel,
									            final Pageable pageable);
    
    
    /**
     * @param trainerId current PT
     * @param keyword   keyword
     * @param pageable  pageable param
     * @return List of Client of PT   
     */
    @Query(
            value = "select (client.uuid), client.avatar, client.full_name, client.num_of_burned, client.num_of_sessions, client.expired_date, client.order_date " + ", client.num_of_confirmed, client.package_uuid "
            		+ "from (select su.uuid, su.avatar, su.full_name, sp.num_of_burned, sp.num_of_sessions, so.expired_date, so.order_date, sp.uuid as package_uuid " + COUNT_NUM_OF_CONFIRMED
                    + "from session_users su left join session_session_packages sp on sp.user_uuid = su.uuid "
                    + "AND sp.status IN (?3) "
                    + "left join session_orders so on so.uuid=sp.order_uuid "
                    + "where sp.pt_uuid = ?1 and su.full_name like %?2% "
                    + "and (so.num_of_burned < so.num_of_sessions) "
                    + "order by if(so.expired_date is null, 9999999999, UNIX_TIMESTAMP(so.expired_date)), so.number_of_limit_day, so.created_date, sp.created_date desc) client "
                    + "group by client.package_uuid "
                    + "\n#pageable\n ",
            countQuery = "select count(client.uuid) from ( "
                    + "select su.uuid "
                    + "from session_users su left join session_session_packages sp on sp.user_uuid = su.uuid "
                    + "left join session_orders so on so.uuid=sp.order_uuid "
                    + "AND sp.status IN (?3) "
                    + "where sp.pt_uuid = ?1 and su.full_name like %?2% "
                    + "and (so.num_of_burned < so.num_of_sessions) "
                    + "order by sp.created_date desc) client",
            nativeQuery = true)
    Page<Object[]> searchPtClientByKeyword(final String trainerId, final String keyword, final List<String> validPackageStatus, final Pageable pageable);

    @Query(value = "SELECT so.uuid as order_uuid, sp.uuid as packageUuid, so.order_code, so.num_of_burned, so.num_of_sessions, " +
            "UNIX_TIMESTAMP(so.expired_date), ss.uuid as session_uuid, ss.status, UNIX_TIMESTAMP(ss.start_time) as start_time, ss.package_uuid, su.uuid as user_uuid, su.avatar, su.full_name, " +
            "st.full_name as trainer_name, so.number_of_limit_day ,st.avatar as trainer_picture ,st.uuid as trainer_uuid " + COUNT_NUM_OF_CONFIRMED +
            "FROM session_session_packages sp JOIN session_orders so ON so.uuid = sp.order_uuid " +
            "LEFT JOIN session_sessions ss ON sp.uuid = ss.package_uuid AND ss.status in :status " +
            "LEFT JOIN session_users su ON sp.user_uuid = su.uuid " +
            "LEFT JOIN session_users st ON ss.pt_uuid = st.uuid " +
            "WHERE sp.pt_uuid = :trainerUuid AND sp.user_uuid = :userUuid ", nativeQuery = true)
    List<Object[]> getContractingHistory(@Param("userUuid") final String userId,
                                         @Param("trainerUuid") final String trainerId,
                                         @Param("status") final List<String> historyShownStatus);
}
