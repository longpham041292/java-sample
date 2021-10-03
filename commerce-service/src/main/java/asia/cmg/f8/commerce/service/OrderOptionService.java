package asia.cmg.f8.commerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import asia.cmg.f8.commerce.entity.OrderOptionEntity;
import asia.cmg.f8.commerce.repository.OrderOptionRepository;

@Service
@Transactional(readOnly = true)
public class OrderOptionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderOptionService.class);
	private static final String PREFIX_DB_ERR = "[QUERY DB] - ";
	
	@Autowired
	private OrderOptionRepository orderOptionRepo;
	
	public List<OrderOptionEntity> getAllOrderOptions() throws Exception {
		
		List<OrderOptionEntity> orderOptions = new ArrayList<OrderOptionEntity>();
		
		try {
			orderOptions = orderOptionRepo.findAll();
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Get all order options failed: {}", e.getMessage());
			throw e;
		}
		
		return orderOptions;
	}
	
	public List<OrderOptionEntity> getByOrderType(final String orderType) throws Exception {
		
		List<OrderOptionEntity> orderOptions = new ArrayList<OrderOptionEntity>();
		
		try {
			orderOptions = orderOptionRepo.getByOrderType(orderType);
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Get all order options failed: {}", e.getMessage());
			throw e;
		}
		
		return orderOptions;
	}
	
	public List<OrderOptionEntity> getByOrderTypeAndLevel(final String orderType, final String levelCode) throws Exception {
		
		List<OrderOptionEntity> orderOptions = new ArrayList<OrderOptionEntity>();
		
		try {
			orderOptions = orderOptionRepo.getByOrderTypeAndLevel(orderType, levelCode);
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Get order options by level code failed: {}", e.getMessage());
			throw e;
		}
		
		return orderOptions;
	}
	
	public OrderOptionEntity getById(final int id) throws Exception {
		
		try {
			Optional<OrderOptionEntity> orderOption =  orderOptionRepo.getById(id);
			if(orderOption.isPresent()) {
				return orderOption.get();
			}
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Get all order options failed: {}", e.getMessage());
			throw e;
		}
		
		return null;
	}
	
	@Transactional(readOnly = false)
	public OrderOptionEntity createOrderOption(final OrderOptionEntity orderOptionEntity) throws Exception {
		
		try {
			if(Objects.isNull(orderOptionEntity)) {
				LOGGER.debug("Invalid OrderOption entity");
				throw new Exception("Invalid OrderOption entity");
			}
			Optional<OrderOptionEntity> orderOption =  orderOptionRepo.getByCode(orderOptionEntity.getCode());
			if(orderOption.isPresent()) {
				LOGGER.debug("OrderOption code already existed");
				throw new Exception("OrderOption code already existed");
			}
			
			return orderOptionRepo.saveAndFlush(orderOptionEntity);
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Insert new OrderOption entity failed: {}", e.getMessage());
			throw e;
		}
	}
	
	@Transactional(readOnly = false)
	public void updateOrderOption(final OrderOptionEntity newOrderOption) throws Exception {
		
		try {
			if(Objects.isNull(newOrderOption)) {
				LOGGER.debug("Invalid OrderOption entity");
				throw new Exception("Invalid OrderOption entity");
			}
			
			Optional<OrderOptionEntity> oldEntity =  orderOptionRepo.getById(newOrderOption.getId());
			if(!oldEntity.isPresent()) {
				LOGGER.debug("OrderOption entity not existed");
				throw new Exception("OrderOption entity not existed");
			}
			
			OrderOptionEntity oldOrderOption = oldEntity.get();
			oldOrderOption.setDescription_en(newOrderOption.getDescription_en());
			oldOrderOption.setDescription_vn(newOrderOption.getDescription_vn());
			oldOrderOption.setOrder_type(newOrderOption.getOrder_type());
			oldOrderOption.setOrdering(newOrderOption.getOrdering());
			oldOrderOption.setVisibility(newOrderOption.getVisibility());
			
			orderOptionRepo.saveAndFlush(oldOrderOption);
		} catch (Exception e) {
			LOGGER.debug(PREFIX_DB_ERR + "Update OrderOption entity failed: {}", e.getMessage());
			throw e;
		}
	}
}
