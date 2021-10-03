package asia.cmg.f8.session.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.session.entity.MemberUploadEntity;



@Repository
public interface MemberUploadRepository extends JpaRepository<MemberUploadEntity, Long>{

	
	 @Query("SELECT me " +
	            "FROM MemberUploadEntity me " +
	            "WHERE me.memberBarcode = :memberBarcode ")
	    List<MemberUploadEntity> getMemberByMemberBarCode(@Param("memberBarcode") final String memberBarcode);
	 
	 
	@Query("select me from MemberUploadEntity me where me.memberBarcode = :memberBarcode "
			+ "and me.userCodechecked = :userCodeChecked ")
	List<MemberUploadEntity> findByMemberBarCodeAndUserCodeChecked(@Param("memberBarcode") final String memberBarcode,
			@Param("userCodeChecked") final Boolean userCodechecked);
	
}
