package asia.cmg.f8.session.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import asia.cmg.f8.session.dto.BasicOrderInfo;
import asia.cmg.f8.session.dto.CreditPackageDTO;
import asia.cmg.f8.session.dto.FreeOrderRequest;
import asia.cmg.f8.session.entity.credit.CreditPackageType;

/**
 * Created on 11/28/16.
 */
@FeignClient(value = "order", url = "${feign.commerceUrl}", fallback = FallbackCommerceClient.class)
public interface FeignCommerceClient extends CommerceClient {

    @RequestMapping(value = "/orders/new?userUuid={userUuid}&ptUuid={ptUuid}&seconds={seconds}",
            method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    List<BasicOrderInfo> getLatestOrderCommerce(@PathVariable(value = "userUuid") final String userUuid,
                                                @PathVariable(value = "ptUuid") final String ptUuid,
                                                @PathVariable(value = "seconds") final Long latestTimestamp);
    
    @RequestMapping(value = "/orders/freeForMigrationUser", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public Integer createFreeOrderMigrationUsers(@RequestBody @Valid final List<FreeOrderRequest> orders);

    /**
     * 
     * @param userUuid
     * @param creditAmount
     * @param transactionType
     * @param description
     * @return Wallet transaction id
     */
    @RequestMapping(value = "/internal/v1/wallets/subtract", method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    long subtractCreditWallet(@RequestParam(name = "userUuid") final String userUuid,
							     @RequestParam(name = "creditAmount") final int creditAmount,
							     @RequestParam(name = "transactionType") final String transactionType,
							     @RequestParam(name = "description") final String description,
							     @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams);
    
    /**
     * 
     * @param userUuid
     * @param creditAmount
     * @param transactionType
     * @param description
     * @return Wallet transaction id
     */
    @RequestMapping(value = "/internal/v1/wallets/plus", method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    long plusCreditWallet(@RequestParam(name = "userUuid") final String userUuid,
						     @RequestParam(name = "creditAmount") final int creditAmount,
						     @RequestParam(name = "transactionType") final String transactionType,
						     @RequestParam(name = "description") final String description,
						     @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams);
    
    /**
     * @param userUuid
     * @param bookingId
     * @param creditAmount
     * @param transactionType
     * @param description
     * @param descriptionParams
     * @return Wallet transaction id
     */
    @RequestMapping(value = "/internal/v1/wallets/subtract", method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    long subtractCreditWallet(@RequestParam(name = "userUuid") final String userUuid,
    							 @RequestParam(name = "bookingId") final long bookingId,
							     @RequestParam(name = "creditAmount") final int creditAmount,
							     @RequestParam(name = "transactionType") final String transactionType,
							     @RequestParam(name = "description") final String description,
							     @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams);
    
    /**
     * @param userUuid
     * @param bookingId
     * @param creditAmount
     * @param transactionType
     * @param description
     * @param descriptionParams
     * @return Wallet transaction id
     */
    @RequestMapping(value = "/internal/v1/wallets/plus", method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    long plusCreditWallet(@RequestParam(name = "userUuid") final String userUuid,
							 @RequestParam(name = "bookingId") final long bookingId,
						     @RequestParam(name = "creditAmount") final int creditAmount,
						     @RequestParam(name = "transactionType") final String transactionType,
						     @RequestParam(name = "description") final String description,
						     @RequestParam(name = "descriptionParams", required = false) List<String> descriptionParams);
    
    /**
     * @author Long
     * @param type
     * @return ResponseEntity<Object> of CreditPackageDTO
     */
    @RequestMapping(value = "/internal/v1/credits/packages", method = RequestMethod.GET, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CreditPackageDTO getCreditPackageByType(@RequestParam(name = "type") CreditPackageType type);
}
