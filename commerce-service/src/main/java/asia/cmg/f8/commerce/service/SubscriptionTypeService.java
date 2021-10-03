package asia.cmg.f8.commerce.service;

import asia.cmg.f8.commerce.dto.SubscriptionTypeDto;
import asia.cmg.f8.commerce.entity.*;
import asia.cmg.f8.commerce.exception.DuplicateSubscriptionTypeException;
import asia.cmg.f8.commerce.repository.LevelEntityRepository;
import asia.cmg.f8.commerce.repository.SubscriptionTypeRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;

/**
 * Subscription Type service
 * @author vinh.nguyen.quang
 *
 */
@Service
public class SubscriptionTypeService {

    @Autowired
    private SubscriptionTypeRepository subscriptionTypeRepository;

    @Autowired
    private LevelEntityRepository levelEntityRepository;

    /**
     * Create new subscription type.
     * @param dto subscription type info
     * @return New subscription type
     */
    @Transactional
    public SubscriptionTypeDto createSubscriptionType(SubscriptionTypeDto dto, String country, String currency) {
        try {
            Optional<LevelEntity> level = levelEntityRepository.findByCode(dto.getLevelCode());
            if (!level.isPresent()) {
                throw new IllegalArgumentException("Invalid subscription level");
            }
            
            Optional<SubscriptionTypeEntity> subsType = subscriptionTypeRepository.findSubscriptionTypeByLevelAndOption(dto.getLevelCode(), dto.getOptionId());
            if (subsType.isPresent()) {
                throw new IllegalArgumentException("Duplicated level code and option id");
            }
            
            SubscriptionTypeEntity entity = new SubscriptionTypeEntity();

            entity.setCountry(country);
            entity.setCurrency(currency);

            entity.setUuid(UUID.randomUUID().toString());
            entity.setLevel(level.get());
            entity.setCommission(CommerceUtils.round(dto.getCommission(), RoundingMode.HALF_UP));
            entity.setUnitPrice(CommerceUtils.roundCurrency(dto.getUnitPrice()));
            entity.setDescription(dto.getDescription());
            entity.setOptionId(dto.getOptionId());

            SubscriptionTypeEntity subscriptionTypeEntity = subscriptionTypeRepository.save(entity);
            return convertEntity(subscriptionTypeEntity);
        } catch (DataIntegrityViolationException exp) {
            throw (DuplicateSubscriptionTypeException) new DuplicateSubscriptionTypeException(
                    "Subscription type already exists").initCause(exp);
        }
    }

    /**
     * Get all subscription type in a country.
     *
     * @return all subscription type of that country
     */
    @Transactional(readOnly = true)
    public List<SubscriptionTypeDto> getAllSubscriptionType() {
        List<SubscriptionTypeEntity> subscriptions = subscriptionTypeRepository.findAll();
        List<SubscriptionTypeDto> result = new ArrayList<>();
        if (!subscriptions.isEmpty()) {
            for (SubscriptionTypeEntity entity : subscriptions) {
                SubscriptionTypeDto dto = convertEntity(entity);

                result.add(dto);
            }
        }
        return result;
    }

    /**
     * Get subscription type by subscription type uuid.
     * @param subscriptionTypeUuid subscription type uuid
     * @return SubscriptionTypeEntity
     */
    @Transactional(readOnly = true)
    public SubscriptionTypeDto getSubscriptionTypeByUuid(String subscriptionTypeUuid) {
        Optional<SubscriptionTypeEntity> subscriptionTypeOpt = subscriptionTypeRepository.findSubscriptionTypeByUuid(subscriptionTypeUuid);
        if (subscriptionTypeOpt.isPresent()) {
            SubscriptionTypeEntity entity = subscriptionTypeOpt.get();

            return convertEntity(entity);
        }
        return null;
    }

    /**
     *
     * @param subscriptionTypeUuid subscription type uuid
     * @param dto subscriptionTypeDto
     * @return update result
     */
    @Transactional
    public SubscriptionTypeDto updateSubscriptionType(String subscriptionTypeUuid, SubscriptionTypeDto dto) throws Exception {
    	try {
    		Optional<SubscriptionTypeEntity> entityOpt = subscriptionTypeRepository.findSubscriptionTypeByUuid(subscriptionTypeUuid);
            if (!entityOpt.isPresent()) {
                throw new IllegalArgumentException("Request subscription Type is not found");
            }

            Optional<LevelEntity> level = levelEntityRepository.findByCode(dto.getLevelCode());
            if (!level.isPresent()) {
                throw new IllegalArgumentException("Invalid subscription level");
            }
            
            SubscriptionTypeEntity entity = entityOpt.get();

            entity.setUnitPrice(CommerceUtils.roundCurrency(dto.getUnitPrice()));
            entity.setDescription(dto.getDescription());
            entity.setCommission(CommerceUtils.round(dto.getCommission(), RoundingMode.HALF_UP));
            entity.setOptionId(dto.getOptionId());
            entity.setLevel(level.get());

            SubscriptionTypeEntity subscriptionType = subscriptionTypeRepository.save(entity);
            return convertEntity(subscriptionType);
		} catch (Exception e) {
			throw e;
		}
    }

    /**
     * Convert entity to dto
     * @param entity
     * @return SubscriptionTypeDto
     */
    private SubscriptionTypeDto convertEntity(SubscriptionTypeEntity entity){
        SubscriptionTypeDto dto = new SubscriptionTypeDto();

        dto.setUuid(entity.getUuid());
        dto.setCommission(entity.getCommission());
        dto.setDescription(entity.getDescription());
        dto.setLevelCode(entity.getLevel().getCode());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setSubscriptionTypeId(entity.getId().toString());
        dto.setOptionId(entity.getOptionId());

        return dto;
    }
}
