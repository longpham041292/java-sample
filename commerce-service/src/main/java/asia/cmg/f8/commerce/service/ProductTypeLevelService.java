package asia.cmg.f8.commerce.service;

import asia.cmg.f8.commerce.dto.Level;
import asia.cmg.f8.commerce.dto.ProductType;
import asia.cmg.f8.commerce.entity.LevelEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;
import asia.cmg.f8.commerce.repository.LevelEntityRepository;
import asia.cmg.f8.commerce.repository.ProductTypeRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Product Type, Product Level service
 * @author tung.nguyenthanh
 *
 */
@Service
public class ProductTypeLevelService {

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private LevelEntityRepository levelEntityRepository;

    /**
     * Get Product/User level with localize data.
     * @param language prefer language
     * @return All levels
     */
    @Transactional(readOnly = true)
    public List<Level> getAllLevels(final String language) {
        final List<Object[]> levelEntities = levelEntityRepository.getAllWithLocalize(language);
        return levelEntities
                .stream()
                .map(entity -> Level.builder().id((String) entity[0]).code((String) entity[1])
                        .text((String) entity[2]).build()).collect(Collectors.toList());
    }

    /**
     * Create new product type.
     * @param dto product type info
     * @param country country that product type belongs to
     * @param currency currency that apply to this product type.
     * @return New product type
     */
    @Transactional
    public ProductTypeEntity createProductType(final ProductType dto, final String country,
            final String currency) {
        final Optional<LevelEntity> level = levelEntityRepository.findByCode(dto.getLevelCode());
        if (!level.isPresent()) {
            throw new IllegalArgumentException("Invalid product level");
        }

        final ProductTypeEntity entity = new ProductTypeEntity();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setLevel(level.get());
        entity.setCommision(CommerceUtils.round(dto.getCommision(), RoundingMode.HALF_UP));
        entity.setUnitPrice(CommerceUtils.roundCurrency(dto.getUnitPrice()));
        entity.setDescription(dto.getDescription());

        entity.setCountry(country);
        entity.setCurrency(currency);

        return productTypeRepository.save(entity);
    }

    /**
     * Update product type.
     * @param dto update info
     * @return updated product type
     */
    @Transactional
    public ProductTypeEntity updateProductType(final ProductType dto) {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new IllegalArgumentException("Product Type uuid is missing");
        }

        final Optional<ProductTypeEntity> entityOpt = productTypeRepository.findOneByUuid(dto
                .getId());
        if (!entityOpt.isPresent()) {
            throw new IllegalArgumentException("Request Product Type is not found");
        }

        final ProductTypeEntity entity = entityOpt.get();
        entity.setCommision(CommerceUtils.round(dto.getCommision(), RoundingMode.HALF_UP));
        entity.setUnitPrice(CommerceUtils.roundCurrency(dto.getUnitPrice()));
        entity.setDescription(dto.getDescription());

        return productTypeRepository.save(entity);
    }
    
    /**
     * Get product type be level per country.
     * @param level product level
     * @param country country that product type belongs to
     * @return Product type
     */
    public ProductTypeEntity getProductTypeByLevel(final String level, final String country) {
        final Optional<ProductTypeEntity> productTypeOpt = productTypeRepository
                .findOneByLevelCodeAndCountry(level, country);
        if (!productTypeOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid product type");
        }
        return productTypeOpt.get();
    }
    
    
    /**
     * Get product type if valid.
     * @param unitPrice unit price
     * @param levelCode level code
     * @param country country
     * @return product type
     */
    public ProductTypeEntity getProductTypeIfValid(final double unitPrice, final String levelCode,
            final String country) {
        final Optional<ProductTypeEntity> productTypeOpt = productTypeRepository
                .findOneByLevelCodeAndCountry(levelCode, country);

        if (productTypeOpt.isPresent()) {
            final ProductTypeEntity entity = productTypeOpt.get();
            if (Double.compare(unitPrice, entity.getUnitPrice().doubleValue()) == 0) {
                return entity;
            }
        }
        return null;
    }
}
