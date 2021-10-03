package asia.cmg.f8.commerce.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.dto.CreditPackageDTO;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.service.CreditPackageSevice;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

@RestController
public class CreditPackageApi {

	@Autowired
	private CreditPackageSevice creditPackageService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreditPackageApi.class);
	
	@GetMapping(value = "/mobile/v1/wallets/credits/packages/language/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> getActiveCreditPackages(@PathVariable(name = "language") final String language, final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditPackageEntity> creditPackages = creditPackageService.getActiveCreditPackages();
			if(language.equalsIgnoreCase("vi")) {
				creditPackages.forEach(creditPackage -> {
					creditPackage.setTitle(creditPackage.getTitleVI());
					creditPackage.getVouchers().forEach(voucher -> {
						voucher.setTitle(voucher.getTitleVI());
					});
				});
			} else {
				creditPackages.forEach(creditPackage -> {
					creditPackage.setTitle(creditPackage.getTitleEN());
					creditPackage.getVouchers().forEach(voucher -> {
						voucher.setTitle(voucher.getTitleEN());
					});
				});
			}
			
			apiResponse.setData(creditPackages);
		} catch (Exception e) {
			LOGGER.error("[getActiveCreditPackages] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PostMapping(value = "/mobile/v1/wallets/credits/packages", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> createCreditPackage(@RequestBody final CreditPackageDTO request) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			CreditPackageEntity creditPackage = creditPackageService.createCreditPackage(request);
			apiResponse.setData(creditPackage);
		} catch (Exception e) {
			LOGGER.error("[createCreditPackage] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@PutMapping(value = "/mobile/v1/wallets/credits/packages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Object> updateCreditPackage(@PathVariable(name = "id") final long id, @RequestBody final CreditPackageDTO request) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			CreditPackageEntity creditPackage = creditPackageService.updateCreditPackage(id, request);
			apiResponse.setData(creditPackage);
		} catch (Exception e) {
			LOGGER.error("[createCreditPackage] error detail: {}", e.getMessage());
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	/**
	 * @author: Long
	 * @param type
	 * @return
	 */
	@GetMapping(value = "/internal/v1/credits/packages", produces = MediaType.APPLICATION_JSON_VALUE)
	public CreditPackageDTO getCreditPackageByType(@RequestParam(name = "type") CreditPackageType type) {
		try {
			return creditPackageService.getCreditPackageByType(type);
		} catch (Exception e) {
			LOGGER.error("[getCreditPackageByType] error detail: {}", e.getMessage());
			return null;
		}
	}
}
