package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.LevelEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelEntityRepository extends JpaRepository<LevelEntity, Long> {

    Optional<LevelEntity> findByCode(final String code);

    @Query(value = "select l.uuid, l.code, dl.localized_value as text from level l left join "
            + "data_localized dl on l.uuid = dl.entity_uuid "
            + "and dl.lang_code = ?1 and dl.localized_key='code' order by l.uuid desc",
            nativeQuery = true)
    List<Object[]> getAllWithLocalize(final String langCode);
}
