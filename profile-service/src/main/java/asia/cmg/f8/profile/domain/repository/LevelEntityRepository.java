package asia.cmg.f8.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.profile.database.entity.LevelEntity;

import java.util.Optional;

@Repository
public interface LevelEntityRepository extends JpaRepository<LevelEntity, Long> {

    Optional<LevelEntity> findByCode(final String code);

}
