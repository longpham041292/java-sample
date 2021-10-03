package asia.cmg.f8.session.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.session.dto.BookingHistoryDTO;
import asia.cmg.f8.session.dto.BookingManagementDTO;
import asia.cmg.f8.session.dto.SpendingStatisticDTO;
import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;

public interface CreditBookingRepository extends JpaRepository<CreditBookingEntity, Long> {

	final String QUERY_EU_SCHEDULED_BOOKINGS=
										"SELECT * " +
										"FROM credit_bookings " +
										"WHERE client_uuid = :clientUuid " +
										"	AND (booking_day BETWEEN :fromDate AND :toDate) " +
										"   AND status NOT IN (1,3) " +
										"	AND id NOT IN (SELECT id FROM credit_bookings temp WHERE booking_type = 0 AND status = 0 AND temp.start_time <= NOW()) " +
										"ORDER BY start_time";

	final String QUERY_PT_SCHEDULED_BOOKINGS =
										"SELECT cb.* " +
										"FROM credit_bookings cb LEFT JOIN credit_session_bookings csb ON cb.id = csb.credit_booking_id " +
										"WHERE cb.status NOT IN (1,3) " +
										"	AND ((client_uuid = :ptUuid AND cb.booking_type IN (1,2)) " +
										"			OR (csb.pt_uuid = :ptUuid AND cb.booking_type IN (0))) " +
										"	AND (booking_day BETWEEN :fromDate AND :toDate	) " +
										"	AND cb.id NOT IN (SELECT id FROM credit_bookings temp WHERE booking_type = 0 AND status = 0 AND temp.start_time <= NOW()) " +
										"ORDER BY cb.start_time";

	@Query(value = QUERY_EU_SCHEDULED_BOOKINGS, nativeQuery = true)
	List<CreditBookingEntity> getEUScheduledBookings(@Param("fromDate") LocalDate startTime,
													@Param("toDate") LocalDate endTime,
													@Param("clientUuid") String clientUuid);

	@Query(value = QUERY_PT_SCHEDULED_BOOKINGS, nativeQuery = true)
	List<CreditBookingEntity> getPTScheduledBookings(@Param("fromDate") LocalDate startTime,
													@Param("toDate") LocalDate endTime,
													@Param("ptUuid") String ptUuid);

	Optional<CreditBookingEntity> findByIdAndBookingTypeAndStatusIn(long id, BookingServiceType bookingType, List<CreditBookingSessionStatus> status);
	
	@Query(value =
			"SELECT cb.* " +
			"FROM credit_bookings cb JOIN credit_session_bookings csb ON cb.id = csb.credit_booking_id " +
			"WHERE cb.status = ?1 AND DATE_ADD(csb.end_time, INTERVAL 15 MINUTE) <= NOW()", nativeQuery = true)
	List<CreditBookingEntity> getConfirmedSessionsOverEndTime(final int status);

	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE id = ?1 AND client_uuid = ?2 AND booking_type IN (1, 2) AND status = ?3 and booking_type = ?4", nativeQuery = true)
	CreditBookingEntity getClassOrEticketBookingByIdAndStatus(long id, String clientUuid, int status, int bookingType);
	
	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE id = ?1 AND client_uuid = ?2 AND booking_type = ?3 AND status IN ?4", nativeQuery = true)
	CreditBookingEntity getClientBookingByIdAndTypeAndStatusIn(long id, String clientUuid, int bookingType, List<Integer> status);
	
	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE id = ?1 AND client_uuid = ?2", nativeQuery = true)
	CreditBookingEntity getBookingByIdAndClient(long id, String clientUuid);
	
	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE id = ?1 AND client_uuid = ?2 AND status = ?3", nativeQuery = true)
	CreditBookingEntity getClientBookingByIdAndStatus(long id, String clientUuid, int status);

	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE status = ?1 and booking_type = ?2 and end_time <= NOW()", nativeQuery = true)
	List<CreditBookingEntity> getCreditBookingByStatusOverEndTime(int status, int bookingType);

	@Query(value =
			"SELECT * " +
			"FROM credit_bookings " +
			"WHERE client_uuid = ?1 AND status = ?2 AND booking_day = ?3 AND booking_type = ?4", nativeQuery = true)
	List<CreditBookingEntity> getCreditBookingByBookingDay(String clientUuid, int status, LocalDate bookingDay, int bookingType);

	@Query(value = "SELECT new asia.cmg.f8.session.dto.BookingHistoryDTO(c) FROM CreditBookingEntity c WHERE clientUuid = ?1 AND createdDate BETWEEN ?2 and ?3 AND c.status in ?4 AND c.bookingType in ?5",
			countQuery = "SELECT COUNT(c.id) FROM CreditBookingEntity c WHERE clientUuid = ?1 AND createdDate BETWEEN ?2 and ?3 AND c.status in ?4 AND c.bookingType in ?5")
	Page<BookingHistoryDTO> getServiceHistoy(final String clientUuid, final LocalDateTime fromDate,
			final LocalDateTime toDate, List<CreditBookingSessionStatus> statuses, List<BookingServiceType> bookingType, final Pageable pageable);

	@Query(value = "SELECT new asia.cmg.f8.session.dto.BookingHistoryDTO(c)  FROM CreditBookingEntity c WHERE clientUuid = ?1 AND createdDate BETWEEN ?2 and ?3 AND c.status in ?4 AND c.bookingType in ?5 AND studioName LIKE %?6% ",
			countQuery = "SELECT COUNT(c.id) FROM CreditBookingEntity c WHERE clientUuid = ?1 AND createdDate BETWEEN ?2 and ?3 AND c.status in ?4 AND c.bookingType in ?5 AND studioName LIKE %?6%")
	Page<BookingHistoryDTO> getServiceHistoyByName(final String clientUuid, final LocalDateTime fromDate,
			final LocalDateTime toDate, List<CreditBookingSessionStatus> statuses, List<BookingServiceType> bookingType, String parterName, final Pageable pageable);

	@Query(value = "SELECT COUNT(*) "
			+ "FROM credit_bookings cb WHERE cb.booking_type = ?3 "
			+ "AND cb.booking_day BETWEEN ?1 AND ?2 "
			+ "AND cb.status IN ?4", nativeQuery = true)
	BigInteger countTotalClubAndClassByStatusCode(LocalDateTime startTime, LocalDateTime endTime, Integer serviceType, List<Integer> statusCode);


	@Query(value = "SELECT COUNT(*) "
			+ "FROM credit_bookings cb WHERE cb.booking_type = ?3 "
			+ "AND cb.booking_day BETWEEN ?1 AND ?2 "
			+ "AND cb.status IN ?4 "
			+ "AND cb.client_uuid = ?5", nativeQuery = true)
	BigInteger countTotalClubAndClassByStatusCodeAndUuid(LocalDateTime startTime, LocalDateTime endTime, Integer serviceType, List<Integer> statusCode, String uuid);


	@Query(value = "SELECT new asia.cmg.f8.session.dto.SpendingStatisticDTO("
			+ "cbe.createdDate, "
			+ "cbe.creditAmount) "
			+ "FROM CreditBookingEntity cbe "
			+ "WHERE cbe.clientUuid = ?3 "
			+ "AND cbe.createdDate BETWEEN ?1 AND ?2 ")
	List<SpendingStatisticDTO> getSpendingStatistic(LocalDateTime start, LocalDateTime end, String uuid);

	@EntityGraph(attributePaths = {"etickets"})
    @Query(value = "select c from CreditBookingEntity c where studioUuid in ?1 and c.status in ?2 and startTime between ?3 and ?4",
    	countQuery = "select count(c.id) from CreditBookingEntity c where studioUuid in ?1 and c.status in ?2 and startTime between ?3 and ?4")
	Page<CreditBookingEntity> findByStudioUuidsAndStatusAndStartTimeBetween(List<String> studioUuid, List<CreditBookingSessionStatus> statuses, LocalDateTime startTimeStart, LocalDateTime startTimeEnd, Pageable pageable);

	@Query(value = "SELECT t "
				+ "FROM CreditBookingEntity t "
				+ "WHERE (clientUuid = ?1) "
				+ "		AND ((startTime <= ?2 AND endTime > ?2) OR (startTime < ?3 AND endTime > ?3)) "
				+ "		AND (status IN ?4) AND (bookingType IN ?5)")
	List<CreditBookingEntity> findOverlappedBookedByClientAndTime(String clientUuid, LocalDateTime startTime, LocalDateTime endTime, List<CreditBookingSessionStatus> statuses, List<BookingServiceType> bookingTypes);

	@Query(value = "Select c.status, count(c.id) From CreditBookingEntity c Where studioUuid = ?1 and startTime between ?2 and ?3 Group by status")
	List<Object[]> countByStudioUuidAndGroupByStatus(String studioUuid, LocalDateTime starTime, LocalDateTime endTime);

	@Query(value = "SELECT cb " +
			"FROM CreditBookingEntity cb " +
			"WHERE cb.studioUuid = ?1 and cb.clientUuid = ?2 and cb.status in ?3 and cb.bookingType = ?4 and cb.bookingDay = ?5")
	List<CreditBookingEntity> getCreditBookingByStudioUuid(String studioUuid, String clientUuid, List<CreditBookingSessionStatus> statuses, BookingServiceType bookingType, LocalDate bookingDay);
	
	@Query(value = "SELECT cb FROM CreditBookingEntity cb WHERE cb.bookingType = 0 AND cb.status = 0 AND cb.startTime <= CURRENT_TIME")
	List<CreditBookingEntity> getListOverdueStartTime();

	@Query(value = "SELECT COUNT(DISTINCT (cb.studio_uuid)) FROM credit_bookings cb WHERE cb.booking_type IN (1,2) "
			+ "AND cb.created_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
	Integer getTotalClubHasBooking(LocalDateTime start, LocalDateTime end);

	List<CreditBookingEntity> findByStudioUuidAndClientUuidAndStatusInAndBookingType(String studioUuid, String clientUuid, List<CreditBookingSessionStatus> statuses, BookingServiceType bookingType);

	List<CreditBookingEntity> findByBookingTypeAndStatusAndStartTimeBetweenAndNotificationReminded(
			BookingServiceType serviceType, CreditBookingSessionStatus status, LocalDateTime startTimeStart,
			LocalDateTime startTimeEnd, boolean notificationReminded);
	
	@Query(value = "SELECT NEW "
			+ " asia.cmg.f8.session.dto.BookingManagementDTO("
			+ "  entity, eu "
			+ " ) "
			+ " FROM CreditBookingEntity entity "
			+ " LEFT JOIN BasicUserEntity eu ON entity.clientUuid = eu.uuid "
			+ " WHERE entity.bookingType IN :type "
			+ " AND entity.status IN :status "
			+ " AND entity.createdDate BETWEEN :start AND :end "
			+ " AND (entity.studioName LIKE %:keyword% OR entity.studioUuid LIKE %:keyword% ) ")
	List<BookingManagementDTO> eticketBookingManagement(
			@Param("keyword") String keyword,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end,
			@Param("status") List<CreditBookingSessionStatus> statusEnum, 
			@Param("type") List<BookingServiceType> type ,Pageable pageable);
	
	List<CreditBookingEntity> findByClientUuidAndBookingType(String clientUuid, BookingServiceType bookingType);
}
