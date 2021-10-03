package asia.cmg.f8.session.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.session.entity.ContractUploadEntity;



@Repository
public interface ContractUploadRepository extends JpaRepository<ContractUploadEntity, Long>{

	@Query("SELECT ce " +
            "FROM ContractUploadEntity ce " +
            "WHERE  ce.contractNumber = :contractNumber ")
    List<ContractUploadEntity> getContracByMemberBarCodeandAndTrainerCodeAndContractNumber(@Param("contractNumber") final String contractNumber);
	
	@Query("SELECT ce " + "FROM ContractUploadEntity ce "
			+ "WHERE ce.memberBarcode = :memberBarcode  AND ce.contractImport = :contractImport")
	List<ContractUploadEntity> findByMemberBarcodeAndContractImport(@Param("memberBarcode") final String memberBarcode, @Param("contractImport") final Boolean contractImport);
	@Query("SELECT ce " + "FROM ContractUploadEntity ce "
			+ "WHERE ce.trainerCode = :trainerCode AND ce.contractImport = :contractImport")
	List<ContractUploadEntity> findByTrainerCodeAndContractImport(@Param("trainerCode") final String trainerCode, @Param("contractImport") final Boolean contractImport);

	@Query("SELECT ce " + "FROM ContractUploadEntity ce " + "WHERE  ce.contractNumber = :contractNumber ")
	List<ContractUploadEntity> findByContractNumber(@Param("contractNumber") final String contractNumber);
	
	@Query(value = "SELECT su.uuid" + "FROM session_users su " + "where su.user_code = :userCode;", nativeQuery = true)
	Optional<String> findUserUuid(@Param("userCode") final String userCode);
}
