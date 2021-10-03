/**
 * 
 */
package asia.cmg.f8.commerce.service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.commerce.client.UserClient;
import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.CommerceErrorConstantCode;
import asia.cmg.f8.commerce.dto.Promotion;
import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.PromotionEntity;
import asia.cmg.f8.commerce.entity.PromotionUsageEntity;
import asia.cmg.f8.commerce.repository.OrderRepository;
import asia.cmg.f8.commerce.repository.PromotionRepository;
import asia.cmg.f8.commerce.repository.PromotionUsageRepository;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.event.OrderCreditCompletedEvent;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * @author khoa.bui
 *
 */
@Service
public class PromotionService {
	private static final Logger LOG = LoggerFactory.getLogger(PromotionService.class);
	
	@Autowired
	private PromotionRepository promotionRepository;
	@Autowired
	private PromotionUsageRepository promotionUsageRepository;
	@Autowired
    private OrderRepository orderRepository;
	@Autowired
    private UserClient userClient;
//	private static final Logger LOG = LoggerFactory.getLogger(PromotionService.class);
	/**
	 * Get Promotion Type with localize data.
	 * 
	 * @return All promotion types
	 */
	@Transactional(readOnly = true)
	public List<Promotion> getPromotions() {
		final List<PromotionEntity> promotionEntities = promotionRepository.findAll();
		return promotionEntities.stream().map(entity -> build(entity)).collect(Collectors.toList());

	}
	
	/**
	 * Get Promotion Type with localize data.
	 * @param couponCode
	 * @return All promotion types
	 */
	@Transactional(readOnly = true)
	public Promotion getPromotionByCouponCode(final String couponCode) {
		final Optional<PromotionEntity> promotionEntities = promotionRepository.findOneByCouponCode(couponCode);
		return build(promotionEntities.get());

	}

	/**
	 * Create a new Promotion
	 * 
	 * @param dto
	 *            Promotion object values
	 * @return Promotion
	 */
	@Transactional
	public Promotion createPromotion(final Promotion dto) {
		final PromotionEntity entity = new PromotionEntity();
		entity.setCouponCode(dto.getCouponCode());
		entity.setDiscount(dto.getDiscount());
		entity.setStartedDate(dto.getStartedDate());
		entity.setEndDate(dto.getEndDate());
		entity.setFreeSession(dto.getFreeSession());
		entity.setActive(dto.getActive());
		entity.setVisibility(dto.getVisibility());
		entity.setMaxIndividualUsage(dto.getMaxIndividualUsage());
		entity.setMaxTotalUsage(dto.getMaxTotalUsage());
		entity.setDesc(dto.getDesc());
		entity.setAppliedGroup(dto.getAppliedGroup());
		entity.setPtCommission(dto.getPtCommission());
		final PromotionEntity entityRef= promotionRepository.save(entity);
		return build(entityRef);
	}

	/**
	 * Update an existed Promotion
	 * 
	 * @param dto
	 *            Promotion object values
	 * @return Promotion
	 */
	@Transactional
	public Promotion updatePromotion(final Promotion dto) {
		
		final PromotionEntity entity = promotionRepository.findOne(dto.getPromtionId());
		if(null != entity) {
			entity.setCouponCode(dto.getCouponCode());
			entity.setDiscount(dto.getDiscount());
			entity.setStartedDate(dto.getStartedDate());
			entity.setEndDate(dto.getEndDate());
			entity.setFreeSession(dto.getFreeSession());
			entity.setActive(dto.getActive());
			entity.setVisibility(dto.getVisibility());
			entity.setMaxIndividualUsage(dto.getMaxIndividualUsage());
			entity.setMaxTotalUsage(dto.getMaxTotalUsage());
			entity.setDesc(dto.getDesc());
			entity.setAppliedGroup(dto.getAppliedGroup());
			entity.setPtCommission(dto.getPtCommission());
			final PromotionEntity entityRef= promotionRepository.save(entity);
			return build(entityRef);
		}
		throw new IllegalArgumentException("Coupon code is not valid.");
		
	}

	@Transactional(readOnly = true)
	public Integer getPromotionCode(final String couponCode, final String userUuid, final String ptUuid, final PaymentProperties paymentProps) {
		
		//Check startdate and enddate
		final Optional<PromotionEntity> promotion = promotionRepository.findProductByCouponCodeAndDate(couponCode, Calendar.getInstance().getTimeInMillis());
		if (!promotion.isPresent()) {
			return CommerceErrorConstantCode.COUPON_CODE_NOT_EXISTED;
		}
		if(null == promotion.get().getActive() || !promotion.get().getActive()) {
			return CommerceErrorConstantCode.COUPON_CODE_INACTIVE;
		}
		final Long numberOfIndividualUsg = promotionUsageRepository.countByUserUuidAndCouponCode(userUuid, couponCode);
		final Integer maxIndividualUsage = promotion.get().getMaxIndividualUsage().intValue();
		//maxIndividualUsage = 0 mean unlimit
		if(null != numberOfIndividualUsg && maxIndividualUsage > 0
			&& maxIndividualUsage <= numberOfIndividualUsg.intValue()) {
			//Individual Usage had reached the limitation
			return CommerceErrorConstantCode.COUPON_CODE_REACH_LIMIT_INDIVIDUAL;	
		}
		final Long numberOfMaxUsg = promotionUsageRepository.countByCouponCode(couponCode);
		final Integer maxTotalUsage = promotion.get().getMaxTotalUsage().intValue();
		//maxTotalUsage = 0 mean unlimit
		if(null != numberOfMaxUsg && maxTotalUsage > 0
			&& maxTotalUsage <= numberOfMaxUsg.intValue()) {
			//Max Usage had reached the limitation
			return CommerceErrorConstantCode.COUPON_CODE_REACH_LIMIT_TOTAL;	
		}
		
		//Check if PT is in the list of allowed group
		LOG.info("Check PT {} in allowed group {}", ptUuid, promotion.get().getAppliedGroup());
		if (StringUtils.isNotEmpty(promotion.get().getAppliedGroup())){
			final UserGridResponse<UserEntity> resp = userClient.getUserInGroup(promotion.get().getAppliedGroup(), "select name where uuid='" + ptUuid + "'");
	        if (resp == null || resp.getEntities().isEmpty()) {
	        	return CommerceErrorConstantCode.COUPON_CODE_NOT_EXISTED;
	        }
		}
		return CommerceErrorConstantCode.COUPON_CODE_VALID;
	}
	
	@Transactional(readOnly = true)
	public ApiRespObject<PromotionEntity> getPromotionCode(final String couponCode, final String userUuid) {
		
		ApiRespObject<PromotionEntity> response = new ApiRespObject<PromotionEntity>();
		response.setStatus(ErrorCode.SUCCESS);
		
		//Check startdate and enddate
		final Optional<PromotionEntity> promotion = promotionRepository.findProductByCouponCodeAndDate(couponCode, Calendar.getInstance().getTimeInMillis());
		
		if (!promotion.isPresent()) {
			response.setStatus(CommerceErrorConstantCode.COUPON_INVALID);
			return response;
		}
		
		if(null == promotion.get().getActive() || !promotion.get().getActive()) {
			response.setStatus(CommerceErrorConstantCode.COUPON_INACTIVE);
			return response;
		}
		
		final Long numberOfIndividualUsg = promotionUsageRepository.countByUserUuidAndCouponCode(userUuid, couponCode);
		final Integer maxIndividualUsage = promotion.get().getMaxIndividualUsage().intValue();
		//maxIndividualUsage = 0 mean unlimit
		if(null != numberOfIndividualUsg && maxIndividualUsage > 0
			&& maxIndividualUsage <= numberOfIndividualUsg.intValue()) {
			//Individual Usage had reached the limitation
			response.setStatus(CommerceErrorConstantCode.COUPON_INVALID);
			return response;
		}
		
		final Long numberOfMaxUsg = promotionUsageRepository.countByCouponCode(couponCode);
		final Integer maxTotalUsage = promotion.get().getMaxTotalUsage().intValue();
		//maxTotalUsage = 0 mean unlimit
		if(null != numberOfMaxUsg && maxTotalUsage > 0
			&& maxTotalUsage <= numberOfMaxUsg.intValue()) {
			//Max Usage had reached the limitation
			response.setStatus(CommerceErrorConstantCode.COUPON_INVALID);
			return response;
		}
		
		response.setData(promotion.get());
		
		return response;
	}
	
	/**
	 * delete an existing promotion
	 * 
	 * @param couponCode
	 * @return Boolean
	 */
	@Transactional
	public Boolean deletePromotion(final String couponCode) {
		final Optional<PromotionEntity> promotion = promotionRepository.findOneByCouponCode(couponCode);
		if (!promotion.isPresent()) {
			throw new IllegalArgumentException("Promotion is not existed.");
		}

		promotionRepository.delete(promotion.get().getId());
		return true;
	}
	
	/**
	 * update an promotion usage
	 * 
	 * @param OrderCompletedEvent
	 */
	@Transactional
	public void updatePromotionUsage(final OrderCompletedEvent event) {
		final Optional<OrderEntity> orderOpt = orderRepository.findOrderByCode(String.valueOf(event.getOrderCode()));
		if(orderOpt.isPresent()) {
			//If product has promotion package
			final String couponCode = orderOpt.get().getCouponCode();
			final Optional<PromotionEntity> promotionEntity = (null == couponCode) ? null :
					promotionRepository.findOneByCouponCode(orderOpt.get().getCouponCode());
			if(null != couponCode &&  !promotionEntity.isPresent()) {
				throw new IllegalArgumentException("update PromotionUsage fail couponCode is not found");
			}
			if(null != couponCode) {
				final PromotionUsageEntity usage = new PromotionUsageEntity();
				usage.setCouponCode(orderOpt.get().getCouponCode());
				usage.setUserUuid(String.valueOf(event.getUserUuid()));
				promotionUsageRepository.save(usage);
			}
		}
	}
	
	@Transactional
	public void updatePromotionUsage(final OrderCreditCompletedEvent event) {
		final OrderEntity orderEntity = orderRepository.findOne(event.getOrderId());
		if(orderEntity != null) {
			//If product has promotion package
			final String couponCode = orderEntity.getCouponCode();
			final Optional<PromotionEntity> promotionEntity = (null == couponCode) ? null :
					promotionRepository.findOneByCouponCode(couponCode);
			
			if(null != couponCode &&  !promotionEntity.isPresent()) {
				throw new IllegalArgumentException("update PromotionUsage fail couponCode is not found");
			}
			
			if(null != couponCode) {
				final PromotionUsageEntity usage = new PromotionUsageEntity();
				usage.setCouponCode(couponCode);
				usage.setUserUuid(String.valueOf(event.getOwnerUuid()));
				promotionUsageRepository.save(usage);
			}
		}
	}
	
	/**
	 * update promotion status
	 * 
	 * @param couponCode
	 * @param status
	 * @return True if successful
	 */
	@Transactional
	public Boolean updatePromotionStatus(final String couponCode, final Boolean status) {
		final Optional<PromotionEntity> promotionEntity = promotionRepository.findOneByCouponCode(couponCode);
		if (!promotionEntity.isPresent()) {
	            throw new IllegalArgumentException("Request Promotion is not found");
	        }

	        final PromotionEntity entity = promotionEntity.get();
	        entity.setActive(status);
	        promotionRepository.save(entity);
	        return Boolean.TRUE;
	}

	public Promotion build(final PromotionEntity entity) {
		if(null == entity) {
			return Promotion.builder().build();
		}
		return Promotion.builder().promtionId(entity.getId()).couponCode(entity.getCouponCode())
				.startedDate(entity.getStartedDate()).endDate(entity.getEndDate()).discount(entity.getDiscount())
				.freeSession(entity.getFreeSession())
				.active(entity.getActive()).visibility(entity.getVisibility())
				.maxIndividualUsage(entity.getMaxIndividualUsage())
				.maxTotalUsage(entity.getMaxTotalUsage())
				.desc(entity.getDesc())
				.appliedGroup(entity.getAppliedGroup())
				.ptCommission(entity.getPtCommission())
				.build();
	}
	
	
	@Transactional
	public Boolean chekPromotionAlreadExist(final String couponCode) {
		final Optional<PromotionEntity> promotion = promotionRepository.findOneByCouponCode(couponCode);
		if (promotion.isPresent()) {
			return true;
		}
		return false;
	}
	
	@Transactional
	public Boolean chekPromotionAlreadExistForUpdate(final Promotion dto) {
		final PromotionEntity entity = promotionRepository.findOne(dto.getPromtionId());
		final Optional<PromotionEntity> promotion = promotionRepository.findOneByCouponCode(dto.getCouponCode());

		if (promotion.isPresent() && promotion.get() != null && entity != null
				&& (promotion.get().getId() != entity.getId())) {
			return true;
		}
		return false;
	}
}
