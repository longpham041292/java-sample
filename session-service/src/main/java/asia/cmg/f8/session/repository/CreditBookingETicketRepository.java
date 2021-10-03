package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.credit.CreditETicketBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditBookingETicketRepository extends JpaRepository<CreditETicketBookingEntity, Long> {

}
