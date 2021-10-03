package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.BookingServiceType;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import asia.cmg.f8.session.entity.credit.CreditClassBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CreditClassBookingRepository extends JpaRepository<CreditClassBookingEntity, Long> {

	@Query(value = "SELECT cbc.service_id, count(cb.id) FROM credit_class_bookings cbc INNER JOIN credit_bookings cb on cb.id = cbc.credit_booking_id WHERE cb.status = 0 and cbc.service_id in ?1 group by cbc.service_id", nativeQuery = true)
	public List<Object[]> countBookingForServices(List<Long> serviceIds);

	@Query(value = "SELECT cbc.service_id, cbc.credit_booking_id FROM credit_class_bookings cbc INNER JOIN credit_bookings cb on cb.id = cbc.credit_booking_id WHERE cb.status = 0 and cb.client_uuid = ?1 and cbc.service_id in ?2", nativeQuery = true)
	public List<Object[]> checkReservedServices(String client_uuid, List<Long> serviceIds);

	CreditClassBookingEntity findByCreditBookingId(Long bookingId);

	@Query(value = "SELECT COUNT(t) " +
			"FROM CreditClassBookingEntity t LEFT JOIN t.creditBooking c " +
			"WHERE t.serviceId = ?1 AND c.status IN ?2 AND c.bookingType IN ?3")
	int countTotalSlotByStatusAndType(long serviceId, List<CreditBookingSessionStatus> statuses, List<BookingServiceType> bookingTypes);

	@Query(value = "SELECT cbc " +
			"FROM CreditClassBookingEntity cbc LEFT JOIN cbc.creditBooking cb " +
			"WHERE cbc.serviceId = ?1 and cb.studioUuid = ?2 and cb.clientUuid = ?3 and cb.status in ?4 and cb.bookingType = ?5")
	List<CreditClassBookingEntity> getClassBookingsByServiceIdAndStudioUuid(Long serviceId, String studioUuid, String clientUuid, List<CreditBookingSessionStatus> statuses, BookingServiceType bookingType);

	@Query(value = "SELECT cbc " +
			"FROM CreditClassBookingEntity cbc LEFT JOIN cbc.creditBooking cb " +
			"WHERE cbc.courseId = ?1 and cb.clientUuid = ?2 and cb.status in ?3 and cb.bookingType = ?4")
	List<CreditClassBookingEntity> getClassBookingsByCourseId(Long courseId, String clientUuid, List<CreditBookingSessionStatus> statuses, BookingServiceType bookingType);

}
