package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.dto.CreditCODRequest;
import asia.cmg.f8.commerce.dto.CreditCODResponse;
import asia.cmg.f8.commerce.dto.CreditCouponsDTO;
import asia.cmg.f8.commerce.dto.UpdateCODRequest;
import asia.cmg.f8.commerce.service.CreditCouponService;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredSalesAdminRole;
import asia.cmg.f8.common.util.PagedResponse;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

/**
 * @author LongPham
 * @email longpham@leep.app
 */
@RestController
public class CreditCouponApi {

	@Autowired
	private CreditCouponService creditCouponService;

	/**
	 * create list credit coupons
	 * 
	 * @param codeRequest
	 * @param account
	 * @return list coupon's serial number and code
	 */
	@RequestMapping(value = "/mobile/v1/wallets/credits/coupons", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredSalesAdminRole
	public ResponseEntity<Object> createListCreditsCoupons(@RequestBody @Valid final CreditCODRequest codeRequest,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditCODResponse> orderResponse = creditCouponService.createListCreditCoupon(codeRequest, account);
			apiResponse.setData(orderResponse);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * get list credit coupons
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@RequestMapping(value = "/mobile/v1/wallets/credits/coupons", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@PreAuthorize("hasRole('sales-admin') || hasRole('sales') || hasRole('admin')")
	public ResponseEntity<Object> getListCreditsCoupons(
			@RequestParam(value = "startTime", required = true) Long startTime,
			@RequestParam(value = "endTime", required = true) Long endTime,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "status", required = false) Integer status,
			@PageableDefault(page = 0, size = 20) Pageable pageable) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			PagedResponse<CreditCouponsDTO> result = creditCouponService.getListCreditsCoupons(startTime, endTime, name,
					status, pageable);
			apiResponse.setData(result);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/mobile/v1/wallets/credits/coupons", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredSalesAdminRole
	public ResponseEntity<Object> updateListCreditsCoupons(@RequestBody @Valid final List<UpdateCODRequest> codeRequest,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		try {
			List<CreditCouponsDTO> orderResponse = creditCouponService.updateListCreditCoupon(codeRequest, account);
			apiResponse.setData(orderResponse);
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
}
