package asia.cmg.f8.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import asia.cmg.f8.profile.domain.entity.home.TrendingEventActionEntity;

public interface TrendingActionRepository extends JpaRepository<TrendingEventActionEntity, Long> {

}
