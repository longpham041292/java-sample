package asia.cmg.f8.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.session.entity.credit.CreditSessionBookingTransactionEntity;

public interface CreditSessionTransactionRepository extends JpaRepository<CreditSessionBookingTransactionEntity, Long> {

}
