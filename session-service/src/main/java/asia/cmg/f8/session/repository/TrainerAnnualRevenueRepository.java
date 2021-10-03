package asia.cmg.f8.session.repository;

import asia.cmg.f8.session.entity.MVTrainerAnnualRevenueEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainerAnnualRevenueRepository extends
        JpaRepository<MVTrainerAnnualRevenueEntity, Long> {

    @Query(value = "select max(ta.lastRun) from MVTrainerAnnualRevenueEntity ta")
    Optional<Object> findMaxByLastRun();

    @Query(
            value = "select ta from MVTrainerAnnualRevenueEntity ta where ta.year = ?1 and ta.ptUuid = ?2")
    Optional<MVTrainerAnnualRevenueEntity> findOneByYearAndPtUuid(final int year,
            final String ptUuid);

}
