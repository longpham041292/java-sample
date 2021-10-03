package asia.cmg.f8.commerce.repository;

import asia.cmg.f8.commerce.entity.CounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 12/30/16.
 */
@Repository
public interface CounterRepository extends JpaRepository<CounterEntity, String> {

}
