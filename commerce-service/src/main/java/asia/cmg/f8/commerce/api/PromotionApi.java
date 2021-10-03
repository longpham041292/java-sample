/**
 * 
 */
package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.CommerceErrorConstantCode;
import asia.cmg.f8.commerce.dto.Promotion;
import asia.cmg.f8.commerce.dto.PromotionCodeResponseDTO;
import asia.cmg.f8.commerce.dto.PromotionExp;
import asia.cmg.f8.commerce.entity.PromotionEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.service.CreditPackageSevice;
import asia.cmg.f8.commerce.service.PromotionService;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.FileExportUtils;
import asia.cmg.f8.common.web.errorcode.ErrorCode;



/**
 * @author khoa.bui
 *
 */
@RestController
public class PromotionApi {
	
	private static final String VALID = "valid";
	private static final String COUPON_CODE = "couponCode";
	private static final String[] MEMBER_HEADER = {"PromoCodes","Discount","PtCommission","FreeSessions","StartDate","EndDate","MaximumIndividualUsage","MaximumTotalUsage","Description","Status" };
	
	@Autowired
    private PromotionService promotionService;

	@Inject
    private CommerceProperties commerceProps;
	
	@Autowired
    private PaymentProperties paymentProps;
	
	@Autowired
	private CreditPackageSevice creditPackageService;

	@RequestMapping(value = "/products/promotion", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> createPromotion(@RequestBody @Valid final Promotion dto) {

		if (promotionService.chekPromotionAlreadExist(dto.getCouponCode())) {
			return new ResponseEntity<>(CommerceErrorConstantCode.COUPON_CODE_DUPLICATE, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Promotion>(promotionService.createPromotion(dto), HttpStatus.OK);

	}

	@RequestMapping(value = "/products/promotion", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> updatePromotion(@RequestBody @Valid final Promotion dto,
			final LanguageContext languageCtx) {
		if (promotionService.chekPromotionAlreadExistForUpdate(dto)) {
			return new ResponseEntity<>(CommerceErrorConstantCode.COUPON_CODE_DUPLICATE, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Promotion>(promotionService.updatePromotion(dto), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/products/promotion/{couponCode}", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Map<String, Boolean>> updatePromotion(@PathVariable(name = COUPON_CODE,
		    required = true) final String couponCode, @RequestBody final Map<String, Boolean> body) {
		final Optional<Boolean> activeOpt = Optional.ofNullable(body.get("active"));
        if (!activeOpt.isPresent()) {
            throw new IllegalArgumentException("active is required for this action.");
        }
        final Boolean result = promotionService.updatePromotionStatus(couponCode, activeOpt.get());
		return new ResponseEntity<Map<String, Boolean>>(
                Collections.singletonMap("SUCCESS", result), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/promotion", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Promotion>> getPromotions() {
		return new ResponseEntity<List<Promotion>>(
				promotionService.getPromotions(), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/promotion/{couponCode}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Promotion> getSinglePromotion(@PathVariable(COUPON_CODE) final String couponCode) {
		return new ResponseEntity<Promotion>(
				promotionService.getPromotionByCouponCode(couponCode), HttpStatus.OK);
	}

	@RequestMapping(value = "/products/promotion/{couponCode}", method = RequestMethod.DELETE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Boolean> deletePromotion(@PathVariable(COUPON_CODE) final String code) {
		return new ResponseEntity<Boolean>(promotionService.deletePromotion(code),
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/products/promotion/valid", method = RequestMethod.GET)
	public Map<String, Object> isValidPromotion(@RequestParam(COUPON_CODE) final String couponCode, final Account account) {
		return Collections.singletonMap(VALID,
				promotionService.getPromotionCode(couponCode, account.uuid(), account.uuid(), paymentProps));
	}
	
	@RequestMapping(value = "/products/promotion/export", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public StreamingResponseBody exportPromotions(final HttpServletResponse response) throws IOException{
		final List<Promotion> promotion = promotionService.getPromotions();
	
		final List<PromotionExp> promotionExp = promotion.stream().map(promo ->
		new PromotionExp(promo)).collect(Collectors.toList());
		
		return FileExportUtils.exportCSV(promotionExp, MEMBER_HEADER, commerceProps.getExport()
                .getMembers(), response);
	}
	
	@GetMapping(value = "/mobile/v1/credit/promotion/package/{package_id}/coupon/{coupon}", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> getPromotionCode(@PathVariable(name = "package_id") final long packageId,
												@PathVariable(name = "coupon") final String couponCode,
												final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		
		try {
			ApiRespObject<PromotionEntity> response = promotionService.getPromotionCode(couponCode, account.uuid());
			if(response.getStatus() == ErrorCode.SUCCESS) {
				PromotionEntity promotion = response.getData();
				CreditPackageEntity creditPackage = creditPackageService.getCreditPackageById(packageId);
				
				if(creditPackage != null) {
					PromotionCodeResponseDTO dto = new PromotionCodeResponseDTO();
					Double discountPercent = promotion.getDiscount();
					Double packagePrice = creditPackage.getPrice();
					double discountPrice = CommerceUtils.calculateDiscountPrice(packagePrice, discountPercent, creditPackage.getCurrency());
					
					dto.setDiscountPercentage(discountPercent);
					dto.setOriginalPrice(packagePrice);
					dto.setPromotionPrice(packagePrice - discountPrice);
					dto.setDiscountPrice(discountPrice);
					dto.setPromotionCode(couponCode);
					
					apiResponse.setData(dto);
				}
			} else {
				apiResponse.setStatus(response.getStatus());
			}
			
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
