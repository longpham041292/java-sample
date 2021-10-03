package asia.cmg.f8.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import asia.cmg.f8.commerce.dto.CreditWalletDTO;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;

public interface CreditWalletRepository extends JpaRepository<CreditWalletEntity, Long> {

	@Modifying
	@Query(value = "UPDATE credit_wallet SET total_credit = total_credit + ?2, available_credit = available_credit + ?2 WHERE id = ?1", nativeQuery = true)
	CreditWalletEntity updateCreditAmount(final long id, final int creditAmount);
	
//	@Query(value = "SELECT * FROM credit_wallet WHERE owner_uuid = ?1 AND active = 1")
//	CreditWalletEntity getCreditWalletByOwnerUuid(final String ownerUuid);
	
	CreditWalletEntity findByIdAndOwnerUuid(final long id, final String ownerUuid);
	
	@Query(value = "SELECT new asia.cmg.f8.commerce.dto.CreditWalletDTO(cw, su.fullName) "
				+ "FROM CreditWalletEntity cw JOIN BasicUserEntity su ON cw.ownerUuid = su.uuid "
				+ "WHERE cw.ownerUuid = ?1")
	CreditWalletDTO getWalletByOwnerUuid(final String ownerUuid);
	
	CreditWalletEntity findByOwnerUuid(final String ownerUuid);
	
	@Query(value = "SELECT * FROM credit_wallets WHERE id = ?1 AND owner_uuid = ?2 AND partner = 1", nativeQuery = true)
	CreditWalletEntity getCMSStudioWallet(final long id, final String ownerUuid);
}
