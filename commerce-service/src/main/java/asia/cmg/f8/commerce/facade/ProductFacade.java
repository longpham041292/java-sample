package asia.cmg.f8.commerce.facade;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.dto.ProductType;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;
import asia.cmg.f8.commerce.exception.DuplicateProductException;
import asia.cmg.f8.commerce.service.ProductService;
import asia.cmg.f8.commerce.service.ProductTypeLevelService;
import asia.cmg.f8.commerce.utils.CommerceUtils;

import org.apache.commons.lang3.StringUtils;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Locale;

import javax.inject.Inject;

@Component
public class ProductFacade {

   // @Inject
    private final ProductService productService;
    
   // @Inject
    private final ProductTypeLevelService typeLevelService;
    
   // @Inject
    private final CommerceProperties commerceProps;

    @Inject
    public ProductFacade(final ProductService productService ,final ProductTypeLevelService typeLevelService, final CommerceProperties commerceProps){
	   
	this.productService = productService;
	this.typeLevelService = typeLevelService;
	this.commerceProps = commerceProps;
   } 
    
    /**
     * Get product type by user level.
     * @param level pt level
     * @param country country
     * @param language language
     * @return productType
     */
	public ProductType getProductTypeByLevel(final String level, final String country,
	        final String language) {
		final ProductTypeEntity productType = typeLevelService.getProductTypeByLevel(
		        level.toUpperCase(Locale.US), country);
		return CommerceUtils.build(productType, language);
	}
    
    /**
     * Create new product with corresponding level.
     * @param dto product DTO
     * @param language display language
     * @return new Product
     */
    public ProductType createProductType(final ProductType dto, final String language) {
        try {
            final ProductTypeEntity entity = typeLevelService.createProductType(dto,
                    commerceProps.getCountry(), commerceProps.getCurrency());
            return CommerceUtils.build(entity, language);
        } catch (final DataIntegrityViolationException exp) {
            throw (DuplicateProductException) new DuplicateProductException(
                    "Product type already exists").initCause(exp);
        }
    }

    public ProductType updateProductType(final ProductType dto, final String language) {
        final ProductTypeEntity entity = typeLevelService.updateProductType(dto);
        return CommerceUtils.build(entity, language);
    }

    /**
     * Create product.
     * @param dto product
     * @param language display localize data.
     * @return new product
     */
    public Product createProduct(final Product dto, final String language) {
        final ProductTypeEntity productType = typeLevelService.getProductTypeByLevel(
                										dto.getLevelCode(), commerceProps.getCountry());
        final ProductEntity entity = productService.createProduct(dto, productType.getCountry(),
                										productType.getCurrency());
        
        return CommerceUtils.buildForAdmin(entity, productType.getUnitPrice(), language);
    }

    /**
     * Update product.
     * @param dto product
     * @param language display localize data.
     * @return updated product
     */
    public Product updateProduct(final Product dto, final String language) {
        if (StringUtils.isEmpty(dto.getId())) {
            throw new IllegalArgumentException("Product uuid is missing");
        }

        final ProductEntity entity = productService.updateProduct(dto);
        final ProductTypeEntity productType = typeLevelService.getProductTypeByLevel(
                dto.getLevelCode(), commerceProps.getCountry());
        return CommerceUtils.buildForAdmin(entity, productType.getUnitPrice(), language);
    }

}
