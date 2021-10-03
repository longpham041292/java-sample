package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.service.InternalWalletCreditService;
import asia.cmg.f8.commerce.service.WalletService;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

@RestController
public class InternalWalletCreditPackageApi {

	@Autowired
	private InternalWalletCreditService internalService;
	
	@Autowired
	private WalletService walletService;
	
	private static final Logger LOG = LoggerFactory.getLogger(InternalWalletCreditService.class);
	
	@PutMapping(value = "/internal/v1/wallets/user-credit-packages/expired", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> processExpiredPurchasedCreditPackaged() {
    	ApiRespObject<Object> apiRespone = new ApiRespObject<Object>();
    	apiRespone.setStatus(ErrorCode.SUCCESS);
    	try {
    		LOG.info("!!! Trigger process expired purchased credit packages at 00:00 everyday by system !!!");
    		internalService.processExpiredPurchasedCreditPackages();
			apiRespone.setData(Collections.singletonMap("success", Boolean.TRUE));
		} catch (Exception e) {
			apiRespone.setData(Collections.singletonMap("success", Boolean.FALSE));
			apiRespone.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			
			LOG.error("[getExpiredPurchasedCreditPackaged] error detail: {}", e.getMessage());
		}
    	
    	return new ResponseEntity<Object>(apiRespone, HttpStatus.OK);
    }
	
	@GetMapping(value = "/internal/v1/wallets/subtract", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> subtractWallet(@RequestParam(name = "userUuid") final String userUuid,
												 @RequestParam(name = "bookingId", required = false, defaultValue = "0") long bookingId,
												 @RequestParam(name = "creditAmount") final int creditAmount,
												 @RequestParam(name = "transactionType") final CreditTransactionType transactionType,
												 @RequestParam(name = "description", required = false) final String description,
												 @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams) {
		long walletTransactionId = 0;
    	try {
    		CreditWalletTransactionEntity transaction = walletService.subtractClientCredits(userUuid, bookingId, creditAmount, transactionType, description, descriptionParams);
    		walletTransactionId = transaction.getId();
		} catch (Exception e) {
			LOG.error("[subtractWallet] Error detail: {}", e.getMessage());
		}
    	
    	return new ResponseEntity<Object>(walletTransactionId, HttpStatus.OK);
	}
	
	@GetMapping(value = "/internal/v1/wallets/plus", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> plusWallet(@RequestParam(name = "userUuid") final String userUuid,
											 @RequestParam(name = "bookingId", required = false, defaultValue = "0") long bookingId,
											 @RequestParam(name = "creditAmount") final int creditAmount,
											 @RequestParam(name = "transactionType") final CreditTransactionType transactionType,
											 @RequestParam(name = "description", required = false) final String description,
											 @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams) {
		long walletTransactionId = 0;
    	try {
    		CreditWalletTransactionEntity transaction = walletService.plusCredits(userUuid, bookingId, creditAmount, transactionType, description, descriptionParams);
    		walletTransactionId = transaction.getId();
		} catch (Exception e) {
			LOG.error("[plusWallet] Error detail: {}", e.getMessage());
		}
    	
    	return new ResponseEntity<Object>(walletTransactionId, HttpStatus.OK);
	}
	/**
	 * @apiNote send notification to user who have credit package about to expire
	 * @author LongPham
	 */
	@PostMapping(value = "/internal/v1/wallets/credit-packages/expired/notification", produces = APPLICATION_JSON_UTF8_VALUE)
	public void expiredUserCreditPackageNotification() {
		try {
			internalService.expiredUserCreditPackageNotification();
		} catch (Exception e) {
			LOG.error("[expiredUserCreditPackageNotification] Error detail: {}", e.getMessage());
		}
	}
}
