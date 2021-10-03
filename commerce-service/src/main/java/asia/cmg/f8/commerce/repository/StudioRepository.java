package asia.cmg.f8.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import asia.cmg.f8.commerce.dto.StudioDto;
import asia.cmg.f8.commerce.entity.credit.StudioEntity;


@Repository
public interface StudioRepository extends JpaRepository<StudioEntity, Integer> {

	final String GET_STUDIO_QUERY_BY_UUID 
    = "SELECT " 
    + "new asia.cmg.f8.commerce.dto.StudioDto("
	+ "studio.id,"
	+ "studio.uuid,"
	+ "studio.name,"
	+ "studio.address,"
	+ "studio.longitude,"
	+ "studio.latitude,"
	+ "studio.image,"
	+ "studio.logo,"
	+ "studio.facebook,"
	+ "studio.website,"
	+ "studio.phone,"
	+ "studio.checkinCredit,"
	+ "studio.openAt,"
	+ "studio.closeAt)"	
	+ " FROM StudioEntity studio"
    + " WHERE studio.uuid =:studio_uuid";
	@Query(value = GET_STUDIO_QUERY_BY_UUID)
	StudioDto getStudioByUuid(@Param("studio_uuid") String studio_uuid);
}
