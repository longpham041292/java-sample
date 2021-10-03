package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.credit.CreditBookingEntity;
import asia.cmg.f8.session.entity.credit.CreditBookingSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.session.entity.credit.CreditBookingTransactionEntity;

import java.util.List;

public interface CreditBookingTransactionRepository extends JpaRepository<CreditBookingTransactionEntity, Long> {
    List<CreditBookingTransactionEntity> findAllByNewStatusAndCreditBookingIn(
            CreditBookingSessionStatus newStatus, List<CreditBookingEntity> creditBookingEntities);
}
