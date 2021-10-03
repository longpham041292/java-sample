package asia.cmg.f8.profile.domain.repository;

import asia.cmg.f8.profile.domain.entity.UserEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by on 10/14/16.
 */
@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<UserEntity, String> {

}
