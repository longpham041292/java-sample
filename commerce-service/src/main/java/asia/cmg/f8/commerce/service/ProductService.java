package asia.cmg.f8.commerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.dto.CreateOrderProductDto;
import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.dto.ProductType;
import asia.cmg.f8.commerce.entity.LevelEntity;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;
import asia.cmg.f8.commerce.entity.PromotionEntity;
import asia.cmg.f8.commerce.repository.LevelEntityRepository;
import asia.cmg.f8.commerce.repository.ProductRepository;
import asia.cmg.f8.commerce.repository.ProductTypeRepository;
import asia.cmg.f8.commerce.repository.PromotionRepository;
import asia.cmg.f8.commerce.utils.CommerceCouponUtils;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private LevelEntityRepository levelEntityRepository;
    
    @Autowired
	private PromotionRepository promotionRepository;

    /**
     * Get all product types of country.
     *
     * @param language return localize language
     * @param country  product types of country
     * @return list product types.
     */
    @Transactional(readOnly = true)
    public List<ProductType> getAllProductTypes(final String country, final String language) {
        final List<ProductTypeEntity> prodTypeEntities = productTypeRepository
                .findAllByCountry(country);
        return prodTypeEntities.stream().map(entity -> CommerceUtils.build(entity, language))
                .collect(Collectors.toList());
    }

    /**
     * Create new product for a country.
     *
     * @param dto      product info
     * @param country  country that new product belongs to (get from correspond product type)
     * @param currency product currency (get from correspond product type)
     * @return new Product
     */
    @Transactional
    public ProductEntity createProduct(final Product dto, final String country,
                                       final String currency) {

        final Optional<LevelEntity> levelOpt = levelEntityRepository.findByCode(dto.getLevelCode());
        if (!levelOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid product level");
        }
        
        ProductTrainingStyle trainingStyle = ProductTrainingStyle.valueOf(dto.getTrainingStyle().toUpperCase());
        if(trainingStyle == null) {
        	throw new IllegalArgumentException("Invalid product training style");
        }

        final ProductEntity entity = new ProductEntity();
        entity.setUuid(UUID.randomUUID().toString());
        entity.setLevel(levelOpt.get());
        entity.setNumOfSessions(dto.getNumOfSessions());
        entity.setPromotionPrice(CommerceUtils.roundCurrency(dto.getPromotionPrice()));
        entity.setExpireLimit(dto.getExpireLimit());

        entity.setActive(dto.getActive());
        entity.setVisibility(Optional.ofNullable(dto.getVisibility()).orElse(Boolean.TRUE));
        entity.setCountry(country);
        entity.setCurrency(currency);
        entity.setTrainingStyle(trainingStyle);

        return productRepository.save(entity);
    }

    /**
     * Update new product for a country.
     *
     * @param dto product info
     * @return updated Product
     */
    @Transactional
    public ProductEntity updateProduct(final Product dto) {
        final Optional<ProductEntity> entityOpt = productRepository.findOneByUuid(dto.getId());
        
        if (!entityOpt.isPresent()) {
            throw new IllegalArgumentException("Request Product Type is not found");
        }
        ProductTrainingStyle trainingStyle = ProductTrainingStyle.valueOf(dto.getTrainingStyle());
        
        if(trainingStyle == null) {
        	throw new IllegalArgumentException("Product training style is invalid");
        }

        final ProductEntity entity = entityOpt.get();
        entity.setNumOfSessions(dto.getNumOfSessions());
        entity.setPromotionPrice(CommerceUtils.roundCurrency(dto.getPromotionPrice()));
        entity.setExpireLimit(dto.getExpireLimit());
        entity.setTrainingStyle(trainingStyle);

        return productRepository.save(entity);
    }

    /**
     * Active/inactive product.
     *
     * @param uuid   product uuid
     * @param active true or false
     * @return update result
     */
    @Transactional
    public Boolean updateProductStatus(final String uuid, final Boolean active) {
        final Optional<ProductEntity> entityOpt = productRepository.findOneByUuid(uuid);
        if (!entityOpt.isPresent()) {
            throw new IllegalArgumentException("Request Product Type is not found");
        }

        final ProductEntity entity = entityOpt.get();
        entity.setActive(active);
        productRepository.save(entity);
        return Boolean.TRUE;
    }

    /**
     * Get all products in a country.
     *
     * @param language format return data
     * @param country  country
     * @return all products of that country
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts(final String language, final String country) {
        final List<ProductEntity> productEntities = productRepository.findAllVisible();
        final List<ProductTypeEntity> productTypes = productTypeRepository.findAllByCountry(country);
        final Map<LevelEntity, Double> levelUnitpriceMap = productTypes.stream().collect(
                Collectors.toMap(ProductTypeEntity::getLevel, ProductTypeEntity::getUnitPrice));
        
        return productEntities
                .stream()
                .map(entity -> CommerceUtils.buildForAdmin(entity, levelUnitpriceMap.get(entity.getLevel()), language))
                .collect(Collectors.toList());
    }

    /**
     * Get product by pt level.
     *
     * @param level    pt
     * @param country  country
     * @param language localize language
     * @param couponCode
     * @return all product of level
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByUserLevel(final String level, final String country,
            									final String language, final String couponCode) {
    	
        final List<ProductEntity> productEntities = productRepository.findByLevel(level, country);
        final Optional<ProductTypeEntity> productTypeOpt = productTypeRepository.findOneByLevelCodeAndCountry(level, country);
        final PromotionEntity promotion = (null == couponCode) ? null : promotionRepository.findOneByCouponCode(couponCode).get();
        
		return productEntities.stream()
				.map(entity -> (null == promotion)
						? CommerceUtils.buildForUser(entity, productTypeOpt.get().getUnitPrice(), language)
						: CommerceCouponUtils.buildForUser(entity, productTypeOpt.get().getUnitPrice(), language, promotion))
				.collect(Collectors.toList());
	}
    
    @Transactional(readOnly = true)
    public List<Product> getProductsByUserLevelAndTraningStyle(final String level, final ProductTrainingStyle trainingStyle, final String country,
            													final String language, final String couponCode) {
    	
        final List<ProductEntity> productEntities = productRepository.findByLevelAndTrainingStyle(level, country, trainingStyle);
        
        final Optional<ProductTypeEntity> productTypeOpt = productTypeRepository.findOneByLevelCodeAndCountry(level, country);
        final PromotionEntity promotion = (null == couponCode) ? null : promotionRepository.findOneByCouponCode(couponCode).get();
        
		return productEntities.stream()
				.map(entity -> (null == promotion)
						? CommerceUtils.buildForUser(entity, productTypeOpt.get().getUnitPrice(), language)
						: CommerceCouponUtils.buildForUser(entity, productTypeOpt.get().getUnitPrice(), language, promotion))
				.collect(Collectors.toList());
	}

    /**
     * Get product by uuid.
     *
     * @param uuid uuid
     * @return Product
     */
    @Transactional(readOnly = true)
    public Product getProductByUuid(final String uuid) {
        final Optional<ProductEntity> entityOpt = productRepository.findOneByUuid(uuid);
        final ProductEntity entity = entityOpt.get();
        if (entity == null) {
            return null;
        }
        return Product.builder().id(uuid).numOfSessions(entity.getNumOfSessions())
                .expireLimit(entity.getExpireLimit())
                .promotionPrice(entity.getPromotionPrice())
                .active(entity.getActive()).build();
    }

    /**
     * Get product if valid.
     *
     * @param dto product info
     * @return product
     */
    @Transactional(readOnly = true)
    public ProductEntity getProductIfValid(final CreateOrderProductDto dto) {
        final Optional<ProductEntity> productEntityOpt = productRepository.findOneByUuid(dto
                .getProductUuid());
        if (productEntityOpt.isPresent()) {
            final ProductEntity entity = productEntityOpt.get();
            if (entity.getActive().equals(Boolean.TRUE)
                    && entity.getNumOfSessions().equals(dto.getNumOfSession())
                    && entity.getPromotionPrice().equals(dto.getPromotionPrice())
                    && entity.getExpireLimit().equals(dto.getExpireLimit())) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Check product is valid or not.
     *
     * @param productId      productId
     * @param level          level
     * @param numOfSession   numOfSession
     * @param promotionPrice promotionPrice
     * @param expireLimit    expireLimit
     * @return checking result
     */
    @Transactional(readOnly = true)
    public boolean isValidProduct(final String productId, final String level,
                                  final int numOfSession, final double promotionPrice, final int expireLimit) {
        final Optional<ProductEntity> productEntity = productRepository.findOneByUuid(productId);
        if (productEntity.isPresent()) {
            final ProductEntity entity = productEntity.get();
            return entity.getActive().equals(Boolean.TRUE)
                    && entity.getLevel().getCode().equals(level)
                    && entity.getNumOfSessions().equals(numOfSession)
                    && entity.getPromotionPrice().equals(promotionPrice)
                    && entity.getExpireLimit().equals(expireLimit);
        }
        return false;
    }
}
