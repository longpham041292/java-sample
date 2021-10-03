package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.ResetAutoFollowEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created on 11/22/16.
 */
@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface ResetAutoFollowRepository extends JpaRepository<ResetAutoFollowEntity, Long>, JpaSpecificationExecutor {
}
