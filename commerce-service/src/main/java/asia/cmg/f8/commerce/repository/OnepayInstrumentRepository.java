package asia.cmg.f8.commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.OnepayInstrumentEntity;

/**
 * Created on 11/25/16.
 */
@Repository
public interface OnepayInstrumentRepository extends CrudRepository<OnepayInstrumentEntity, String> {

	Optional<OnepayInstrumentEntity> findOneByMerchantTxnRef(final String merchantTxnRef);

	@Query(value = "SELECT i.* FROM onepay_instruments i " 
			+ "JOIN onepay_users u on i.user_id = u.id "
			+ "WHERE u.user_uuid = ?1 ", nativeQuery = true)
	List<OnepayInstrumentEntity> findInstrumentByUserId(final String userUuid);
	
	@Query(value = "SELECT i.* FROM onepay_instruments i " 
			+ "JOIN onepay_users u on i.user_id = u.id "
			+ "WHERE u.user_uuid = ?1 AND i.psp_id=?2", nativeQuery = true)
	List<OnepayInstrumentEntity> findInstrumentByUserIdAndPspId(final String userUuid, final String pspId);
	
	Optional<OnepayInstrumentEntity> findOneById(final Long id);
}
