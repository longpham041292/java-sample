package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.client.UserClient;
import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.CommerceErrorConstantCode;
import asia.cmg.f8.commerce.dto.Level;
import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.dto.ProductType;
import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.commerce.facade.ProductFacade;
import asia.cmg.f8.commerce.service.ProductService;
import asia.cmg.f8.commerce.service.ProductTypeLevelService;
import asia.cmg.f8.commerce.service.PromotionService;
import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;
import asia.cmg.f8.common.util.UserGridResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

@RestController
public class ProductApi {
    private static final String VALID = "valid";
    private static final String UUID = "uuid";

    @Autowired
    private ProductService productService;
    
    @Autowired
    private PromotionService promotionService;

    @Autowired
    private ProductTypeLevelService typeLevelService;


    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CommerceProperties commerceProps;
    
    @Autowired
    private PaymentProperties paymentProps;
//    private static final Logger LOG = LoggerFactory.getLogger(ProductApi.class);

    @RequestMapping(value = "/products/levels", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Level>> getLevels(final LanguageContext language) {
        return new ResponseEntity<>(typeLevelService.getAllLevels(language.language()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/products/types", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<ProductType> createProductType(@RequestBody @Valid final ProductType dto,
            final LanguageContext language) {
        return new ResponseEntity<>(productFacade.createProductType(dto,
                language.language()), HttpStatus.OK);
    }

    @RequestMapping(value = "/products/types", method = RequestMethod.PUT,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<ProductType> updateProductType(@RequestBody @Valid final ProductType dto,
            final LanguageContext language) {
        return new ResponseEntity<>(productFacade.updateProductType(dto,
                language.language()), HttpStatus.OK);
    }

    @RequestMapping(value = "/products/types", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<ProductType>> findAllProductTypes(
            final LanguageContext languageCtx) {
        return new ResponseEntity<>(productService.getAllProductTypes(
                commerceProps.getCountry(), languageCtx.language()), HttpStatus.OK);
    }

    /**
     * Create new product by Admin role
     * @param dto
     * @param languageCtx
     * @return Product object
     */
    @RequestMapping(value = "/products", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Product> createProduct(@RequestBody @Valid final Product dto,
            									final LanguageContext languageCtx) {
    	
        return new ResponseEntity<>(productFacade.createProduct(dto, languageCtx.language()), HttpStatus.OK);
    }

    @RequestMapping(value = "/products", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid final Product dto,
        										final LanguageContext languageCtx) {
    	
        return new ResponseEntity<>(productFacade.updateProduct(dto, languageCtx.language()), HttpStatus.OK);
    }

    /**
     * Active/Inactive product.
     * @param uuid product uuid
     * @param body active field
     * @return result
     */
    @RequestMapping(value = "/products/{uuid}", method = RequestMethod.PUT,
            consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<Map<String, Boolean>> updateProductStatus(@PathVariable(name = UUID, required = true) final String uuid, 
    																@RequestBody final Map<String, Boolean> body) {
        final Optional<Boolean> activeOpt = Optional.ofNullable(body.get("active"));
        if (!activeOpt.isPresent()) {
            throw new IllegalArgumentException("active is required for this action.");
        }

        final Boolean result = productService.updateProductStatus(uuid, activeOpt.get());
        return new ResponseEntity<>(
                Collections.singletonMap("SUCCESS", result), HttpStatus.OK);
    }

    /**
     * Get all Products
     * @param languageCtx
     * @return List<Product> object
     */
    @RequestMapping(value = "/products", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<List<Product>> findAllProducts(final LanguageContext languageCtx) {
    	
        return new ResponseEntity<>(productService.getAllProducts(languageCtx.language(), commerceProps.getCountry()), HttpStatus.OK);
    }


    @RequestMapping(value = "/products/users/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Product>> getProductsByUser(@PathVariable(UUID) final String ptUuid,
    														@RequestParam(name = "trainingStyle", required = false) String trainingStyle,
    														final Account account, final LanguageContext languageCtx) {
    	
        final UserGridResponse<UserEntity> resp = userClient.getUser(ptUuid);
        if (resp.getEntities().isEmpty()) {
            throw new IllegalArgumentException("User is invalid");
        }

        final String level = resp.getEntities().get(0).getLevel();
        if (StringUtils.isEmpty(level)) {
            throw new IllegalArgumentException("User level not found");
        }
        
        ProductTrainingStyle productTrainingStyle = trainingStyle == null ? ProductTrainingStyle.OFFLINE : ProductTrainingStyle.valueOf(trainingStyle.toUpperCase());
        if(productTrainingStyle == null) {
        	throw new IllegalArgumentException("Training style value is invalid");
        }

        List<Product> products = productService.getProductsByUserLevelAndTraningStyle(level.toUpperCase(Locale.ENGLISH), productTrainingStyle, commerceProps.getCountry(),languageCtx.language(), null);
        
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/products/users/promotion/{uuid}/{couponCode}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Product>> getProductsByUserAndCouponCode(@PathVariable(UUID) final String ptUuid,  
    									@PathVariable("couponCode") final String couponCode, 
    									@RequestParam(name = "trainingStyle", required = false) String trainingStyle,
    									final Account account, final LanguageContext languageCtx) {
    	
        final UserGridResponse<UserEntity> resp = userClient.getUser(ptUuid);
        if (resp.getEntities().isEmpty()) {
            throw new IllegalArgumentException("User is invalid");
        }

        final String level = resp.getEntities().get(0).getLevel();
        if (StringUtils.isEmpty(level)) {
            throw new IllegalArgumentException("User level not found");
        }
        
        ProductTrainingStyle productTrainingStyle = trainingStyle == null ? ProductTrainingStyle.OFFLINE : ProductTrainingStyle.valueOf(trainingStyle.toUpperCase());
        if(productTrainingStyle == null) {
        	throw new IllegalArgumentException("Training style value is invalid");
        }
        
        final int promotionCode = promotionService.getPromotionCode(couponCode, account.uuid(), ptUuid, paymentProps);
        if(CommerceErrorConstantCode.COUPON_CODE_VALID != promotionCode) {
			return new ResponseEntity(new ErrorCode(promotionCode, "INVALID_COUPON_CODE", "Invalid coupon code"),
					HttpStatus.BAD_REQUEST);
        }
        
        List<Product> products = productService.getProductsByUserLevelAndTraningStyle(level.toUpperCase(Locale.ENGLISH), productTrainingStyle, commerceProps.getCountry(),languageCtx.language(), couponCode);
        
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @RequestMapping(value = "/product-type/users/{uuid}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    @RequiredAdminRole
    public ResponseEntity<?> getProductTypeInfoByUser(@PathVariable(UUID) final String ptUuid,
            final Account account, final LanguageContext languageCtx) {
        final UserGridResponse<UserEntity> resp = userClient.getUser(ptUuid);
        if (resp.getEntities().isEmpty()) {
            throw new IllegalArgumentException("User is invalid");
        }
        final String level = resp.getEntities().get(0).getLevel();
        if (StringUtils.isEmpty(level)) {
            throw new IllegalArgumentException("User level not found");
        }

        return new ResponseEntity<>(productFacade.getProductTypeByLevel(level,
                commerceProps.getCountry(), languageCtx.language()), HttpStatus.OK);
    }

    @RequestMapping(value = "/products/valid", method = RequestMethod.GET)
    public Map<String, Object> isValidProduct(@RequestParam("product_uuid") final String productId,
            @RequestParam("level") final String level,
            @RequestParam("num_of_session") final int numOfSession,
            @RequestParam("promotion_price") final double price,
            @RequestParam("expire_limit") final int expireLimit) {
        return Collections.singletonMap(VALID,
                productService.isValidProduct(productId, level, numOfSession, price, expireLimit));
    }

}
