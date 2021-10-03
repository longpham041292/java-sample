package asia.cmg.f8.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import asia.cmg.f8.commerce.dto.WalletActivityDto;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageTransactionEntity;

public interface UserCreditPackageTransationRepository extends JpaRepository<UserCreditPackageTransactionEntity, Long> {
	List<UserCreditPackageTransactionEntity> findByCreditWalletTransactionId(Long walletTransactionId);
}
