package asia.cmg.f8.session.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.session.entity.TrainerUploadEntity;


@Repository
public interface TrainerUploadRepository extends JpaRepository<TrainerUploadEntity, Long>{

	
	@Query("SELECT te " +
            "FROM TrainerUploadEntity te " +
            "WHERE te.trainerCode = :trainerCode ")
    List<TrainerUploadEntity> getTrainerByTrainerCode(@Param("trainerCode") final String trainerCode);
	
	@Query("select te from TrainerUploadEntity te where te.trainerCode = :trainerCode "
			+ "and te.userCodechecked = :userCodechecked ")
	List<TrainerUploadEntity> findByTrainerCodeAndUserCodeChecked(@Param("trainerCode") final String trainerCode,
			@Param("userCodechecked") final Boolean userCodechecked);
}
