/**
 * 
 */
package asia.cmg.f8.commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.LocalizedData;

/**
 * @author khoa.bui
 *
 */
@Repository
public interface LocalizedDataRepository extends JpaRepository<LocalizedData, Long> {

	Optional<LocalizedData> findByLocalizedKeyAndLangCode(final String localizedKey, final String lang);
}
