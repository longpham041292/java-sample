package asia.cmg.f8.commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asia.cmg.f8.commerce.entity.OrderOptionEntity;

@Repository
public interface OrderOptionRepository extends JpaRepository<OrderOptionEntity, Integer> {
	
	@Query(value = "SELECT * FROM order_options WHERE visibility = 1 ORDER BY ordering ASC", nativeQuery = true)
	List<OrderOptionEntity> findAll();	
	
	@Query(value = "SELECT * FROM order_options WHERE id = ?1", nativeQuery = true)
	Optional<OrderOptionEntity> getById(final Integer id);
	
	@Query(value = "SELECT * FROM order_options WHERE code = ?1", nativeQuery = true)
	Optional<OrderOptionEntity> getByCode(final String code);
	
	@Query(value = "SELECT * FROM order_options WHERE order_type = ?1 AND visibility = 1 ORDER BY ordering ASC", nativeQuery = true)
	List<OrderOptionEntity> getByOrderType(final String orderType);
	
	@Query(value = "SELECT * FROM order_options WHERE order_type = ?1 AND level_code = ?2 AND visibility = 1 ORDER BY ordering ASC", nativeQuery = true)
	List<OrderOptionEntity> getByOrderTypeAndLevel(final String orderType, final String levelCode);
}
