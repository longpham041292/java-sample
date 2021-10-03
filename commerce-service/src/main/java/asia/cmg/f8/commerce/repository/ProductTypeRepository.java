package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.LevelEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductTypeEntity, Long> {


    Optional<ProductTypeEntity> findOneByUuid(final String uuid);

    Optional<ProductTypeEntity> findOneByLevelAndCountry(final LevelEntity level,
            final String country);

    @Query("select type from ProductTypeEntity type where type.level.code = ?1 "
            + "and type.country = ?2")
    Optional<ProductTypeEntity> findOneByLevelCodeAndCountry(final String levelCode,
            final String country);
    
    @Query("select type from ProductTypeEntity type where type.country = ?1")
    List<ProductTypeEntity> findAllByCountry(final String country);
}
