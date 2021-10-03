package asia.cmg.f8.session.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import asia.cmg.f8.session.entity.UserMessageEntity;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessageEntity, Long>{

	
	@Query("SELECT um " +
            "FROM UserMessageEntity um " +
            "WHERE um.languageCode = :languageCode ")
    List<UserMessageEntity> getMessageByLanguageCode(@Param("languageCode") final String languageCode);
	
}
