package asia.cmg.f8.commerce.service;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.dto.SubscriptionDto;
import asia.cmg.f8.commerce.entity.SubscriptionTypeEntity;
import asia.cmg.f8.commerce.repository.SubscriptionRepository;
import asia.cmg.f8.commerce.repository.SubscriptionTypeRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.entity.SubscriptionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Subscription Type service
 * @author vinh.nguyen.quang
 *
 */
@Service
public class SubscriptionService {
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private SubscriptionTypeRepository subscriptionTypeRepository;

	@Autowired
	private CommerceProperties commerceProps;

	enum STATUS {
		ACTIVE,
		INACTIVE
	}

	/**
	 * Create new Subscription.
	 * @param dto Subscription info
	 * @return New Subscription
	 */
	@Transactional
	public SubscriptionDto createSubscription(SubscriptionDto dto, String country, String currency) {
		SubscriptionEntity entity = new SubscriptionEntity();

		entity.setUuid(UUID.randomUUID().toString());
		entity.setNumberOfMonth(dto.getNumberOfMonth());
		entity.setLimitDay(dto.getLimitDay());
		entity.setPrice(CommerceUtils.roundCurrency(dto.getPrice()));
		entity.setStatus(STATUS.ACTIVE.toString());
		entity.setSubscriptionTypeId(dto.getSubscriptionTypeId());

		entity.setCountry(country);
		entity.setCurrency(currency);

		SubscriptionEntity subscriptionEntity = subscriptionRepository.save(entity);
		return convertEntity(subscriptionEntity);
	}

	/**
	 * Get all subscription in a country.
	 *
	 * @return all subscription of that country
	 */
	@Transactional(readOnly = true)
	public List<SubscriptionDto> getAllSubscriptions() {
		List<SubscriptionEntity> subscriptions = subscriptionRepository.findAll();
		List<SubscriptionDto> result = new ArrayList<>();
		if (!subscriptions.isEmpty()) {
			for (SubscriptionEntity entity : subscriptions) {
				SubscriptionDto dto = convertEntity(entity);

				result.add(dto);
			}
		}
		return result;
	}

	/**
	 * Get subscription by subscription uuid.
	 * @param subscriptionUuid subscription uuid
	 * @return SubscriptionEntity
	 */
	@Transactional(readOnly = true)
	public SubscriptionDto getSubscriptionDtoByUuid(String subscriptionUuid) {
		Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findSubscriptionByUuid(subscriptionUuid);
		if (subscriptionOpt.isPresent()) {
			SubscriptionEntity entity = subscriptionOpt.get();

			return convertEntity(entity);
		}
		return null;
	}

	/**
	 * Get Subscription by subscription uuid.
	 * @param subscriptionUuid subscription uuid
	 * @return SubscriptionEntity
	 */
	@Transactional(readOnly = true)
	public SubscriptionEntity getSubscriptionByUuid(String subscriptionUuid) {
		Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findSubscriptionByUuid(subscriptionUuid);
		if (subscriptionOpt.isPresent()) {
			SubscriptionEntity entity = subscriptionOpt.get();
			return entity;
		}
		return null;
	}

	/**
	 * Get subscription by user uuid.
	 *
	 * @param level
	 * @param country
	 * @return List<SubscriptionDto>
	 */
	@Transactional(readOnly = true)
	public List<SubscriptionDto> getSubscriptionByUserLevel(String level, String country) {
		List<SubscriptionDto> result = new ArrayList<>();
		List<SubscriptionEntity> subscriptions = subscriptionRepository.findByLevel(level, country);
		if (!subscriptions.isEmpty()) {
			for (SubscriptionEntity entity : subscriptions) {
				SubscriptionDto dto = convertEntity(entity);
				result.add(dto);
			}
			return result;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<SubscriptionDto> getSubscriptionByUserLevelAndOptionId(final String country, final String levelCode, final Integer optionId) {
		List<SubscriptionDto> result = new ArrayList<>();
		List<SubscriptionEntity> subscriptions = subscriptionRepository.findByLevelAndOption(country, levelCode, optionId);
		if (!subscriptions.isEmpty()) {
			for (SubscriptionEntity entity : subscriptions) {
				SubscriptionDto dto = convertEntity(entity);
				result.add(dto);
			}
			return result;
		}
		return null;
	}
	
	/**
	 *
	 * @param subscriptionUuid subscription uuid
	 * @param dto SubscriptionDto
	 * @return update result
	 */
	@Transactional
	public SubscriptionDto updateSubscription(String subscriptionUuid, SubscriptionDto dto) {
		Optional<SubscriptionEntity> entityOpt = subscriptionRepository.findSubscriptionByUuid(subscriptionUuid);
		if (!entityOpt.isPresent()) {
			throw new IllegalArgumentException("Request Subscription is not found");
		}

		SubscriptionEntity entity = entityOpt.get();

		for (STATUS status : STATUS.values()) {
			if (status.name().equals(dto.getStatus())) {
				entity.setStatus(dto.getStatus());
			}
		}

		entity.setNumberOfMonth(dto.getNumberOfMonth());
		entity.setLimitDay(dto.getLimitDay());
		entity.setPrice(CommerceUtils.roundCurrency(dto.getPrice()));

		SubscriptionEntity subscription = subscriptionRepository.save(entity);
		return convertEntity(subscription);
	}

	/**
	 * Convert entity to dto
	 * @param entity
	 * @return SubscriptionDto
	 */
	private SubscriptionDto convertEntity(SubscriptionEntity entity){
		SubscriptionDto dto = new SubscriptionDto();

		Double unitPrice = 0.0;
		Double price = 0.0;
		Double discount = 0.0;
		int quantity = 0;

		SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.findOne(entity.getSubscriptionTypeId());
		if(subscriptionTypeEntity != null) {

			unitPrice = subscriptionTypeEntity.getUnitPrice();
			dto.setLevel(subscriptionTypeEntity.getLevel().getCode());
			String description = subscriptionTypeEntity.getDescription();
			dto.setDescription(description != null ? description : "");
		}

		quantity = entity.getNumberOfMonth();
		price = entity.getPrice();

		dto.setUuid(entity.getUuid());
		dto.setNumberOfMonth(quantity);
		dto.setLimitDay(entity.getLimitDay());
		dto.setPrice(price);
		dto.setStatus(entity.getStatus());
		dto.setSubscriptionTypeId(entity.getSubscriptionTypeId());

		discount = CommerceUtils.calculateDiscount(unitPrice, quantity, price);
		dto.setDiscount(Double.valueOf(Math.round(discount)));
		dto.setSubTotal(unitPrice * quantity);
		
		dto.setCurrency(commerceProps.getCurrency());
		
		

		return dto;
	}

	/**
	 *
	 * @param subscriptionTypeId
	 * @return Subscription Type Exist
	 */
	public Boolean checkSubscriptionTypeExist(Long subscriptionTypeId){
		SubscriptionTypeEntity entity = subscriptionTypeRepository.findOne(subscriptionTypeId);
		if (entity == null) return false;
		return true;
	}
}
