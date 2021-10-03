package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("select p from ProductEntity p where p.visibility = true")
    List<ProductEntity> findAllVisible();

    @Query("select p from ProductEntity p where p.uuid = ?1 and p.visibility = true")
    Optional<ProductEntity> findOneByUuid(final String uuid);

    @Query("select p from ProductEntity p where p.level.code = ?1 and p.country = ?2 "
            + "and p.active = true and p.visibility = true order by p.promotionPrice asc")
    List<ProductEntity> findByLevel(final String level, final String country);
    
    @Query(value = "SELECT p FROM ProductEntity p WHERE p.level.code = ?1 AND p.country = ?2 AND p.trainingStyle = ?3 "
			+ "AND p.active = true and p.visibility = true ORDER BY p.promotionPrice ASC")
    List<ProductEntity> findByLevelAndTrainingStyle(final String level, final String country, final ProductTrainingStyle trainingStyle);
}
