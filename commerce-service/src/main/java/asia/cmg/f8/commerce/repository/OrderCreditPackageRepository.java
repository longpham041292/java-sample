package asia.cmg.f8.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;

public interface OrderCreditPackageRepository extends JpaRepository<OrderCreditEntryEntity, Long> {

	@Query(value = "SELECT * FROM order_credit_entries oce WHERE oce.order_id = ?1", nativeQuery = true)
	OrderCreditEntryEntity findByOrderId(Long orderId);
}
