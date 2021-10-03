package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import asia.cmg.f8.commerce.dto.Order;
import asia.cmg.f8.commerce.dto.OrderBasicInfo;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;
import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.facade.OrderInternalFacade;
import asia.cmg.f8.commerce.facade.PaymentFacade;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.service.CreditWalletTransactionService;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.commerce.service.PaymentService;
import asia.cmg.f8.commerce.service.UserService;
import asia.cmg.f8.commerce.service.WalletService;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.spec.order.PaymentStatus;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * All APIs that only support for internal call will be here
 * @author tung.nguyenthanh
 *
 */
@RestController
public class InternalOrderApi {

   // @Inject
    private final OrderService orderService;
    
    //@Inject
    private final OrderInternalFacade orderInternalFacade;
    
   // @Inject
    private final PaymentFacade paymentFacade;
    
    @Autowired
    private UserService userService;
    
    @Autowired
	private CreditWalletTransactionService walletTransactionService;
	
	@Autowired
	private UserCreditPackageRepository creditPackageOwnerRepo;
	
	@Autowired
	private WalletService walletService;
	
	private static final Logger LOG = LoggerFactory.getLogger(InternalOrderApi.class);
    
    @Inject
    public InternalOrderApi(final OrderService orderService, final OrderInternalFacade orderInternalFacade, final PaymentFacade paymentFacade){
    	this.orderService = orderService;
    	this.orderInternalFacade = orderInternalFacade;
    	this.paymentFacade = paymentFacade;
    }
    
    @RequestMapping(value = "/system/orders/{uuid}", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Order> getOrderBySystem(
            @PathVariable(name = "uuid", required = true) final String orderUuid) {
        return new ResponseEntity<>(orderInternalFacade.getOrderByUuid(orderUuid),
                HttpStatus.OK);
    }
    

    @RequestMapping(value = "/orders/new", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OrderBasicInfo>> getNewOrder(@RequestParam(name = "userUuid",
            required = true) final String userUuid,
            @RequestParam(name = "ptUuid", required = true) final String ptUuid, @RequestParam(
                    name = "seconds", required = false) final Long seconds) {
        final LocalDateTime fromDate = seconds == null ? null : ZoneDateTimeUtils
                .convertFromUTCToLocalDateTime(seconds);
        return new ResponseEntity<>(orderInternalFacade.searchNewOrder(userUuid,
                ptUuid, fromDate), HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/system/orders/pending", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getOrderPending(
            @RequestParam("queryAfter") final int queryAfterMins) {
        return new ResponseEntity<>(orderService.getOrderByPaymentStatus(
                PaymentStatus.WAITING_PAYMENT, queryAfterMins), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/system/orders/{orderUuid}/query", method = RequestMethod.GET)
    public ResponseEntity<Boolean> queryOrderPayment(
            @PathVariable("orderUuid") final String orderUuid) {
        return new ResponseEntity<>(paymentFacade.queryPaymentStatus(orderUuid), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/internal/v1/orders/credits/re-ordering", method = RequestMethod.GET)
    public ResponseEntity<Object> reOrderingForFailedTransaction(@RequestParam("orderCode") Set<String> orderCodes) {
    	boolean success = true;
    	try {
    		orderCodes.stream().forEach(orderCode -> {
    			final OrderEntity order = orderService.getOrderByCode(orderCode);
    			
    			if(OrderType.CREDIT.name().compareToIgnoreCase(order.getType()) == 0) {
    				OrderCreditEntryEntity orderCreditPackage = order.getOrderCreditPackageEntries().get(0);
    				CreditPackageEntity creditPackage = orderCreditPackage.getCreditPackage();
    				
    				int creditAmount = (creditPackage.getCredit() + creditPackage.getBonusCredit()) * orderCreditPackage.getQuantity();
    				double amount = orderCreditPackage.getQuantity() * orderCreditPackage.getUnitPrice();
    				String ownerUuid = orderCreditPackage.getOwnerUuid();
    				CreditTransactionType transactionType = CreditTransactionType.TOPUP;
    				CreditTransactionStatus transactionStatus = CreditTransactionStatus.COMPLETED;
    				String description = CreditTransactionType.TOPUP.getText() + "_" + creditPackage.getId() + "_" + creditAmount;
    				BasicUserEntity userInfo = userService.getOneByUuid(ownerUuid);
    				
    				CreditWalletEntity creditWallet = walletService.getWalletByOwnerUuid(ownerUuid);
    				if(creditWallet == null) {
    					Boolean active = Boolean.TRUE;
    					creditWallet = new CreditWalletEntity(ownerUuid, active);
    					try {
    						walletService.createWallet(creditWallet);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    				
    				try {
    					int currentCreditBalance = creditWallet.getTotalCredit();
    					CreditWalletLevel currentWalletLevel = creditWallet.getLevel();
    					
    					// Topup credit to user wallet
    					creditWallet = walletService.topupCreditWallet(ownerUuid, creditAmount, amount);
    					CreditWalletLevel newWalletLevel = creditWallet.getLevel();
    					
    					// Recording the TOPUP credit wallet transaction
    					List<String> descriptionParams = Arrays.asList(String.valueOf(creditPackage.getCreditType().name()));
    					walletTransactionService.createCreditWalletTransaction(creditWallet.getId(), ownerUuid, userInfo.getAvatar(), 
    																		currentCreditBalance, creditAmount, transactionType, 
    																		transactionStatus, description, descriptionParams);

    					// Recording the UPGRADE WALLET LEVEL if any
    					if(newWalletLevel != currentWalletLevel) {
    						descriptionParams = Arrays.asList(String.valueOf(newWalletLevel.name()));
    						transactionType = CreditTransactionType.WALLET_LEVEL_UPGRADE;
    						walletTransactionService.createCreditWalletTransaction(creditWallet.getId(), ownerUuid, userInfo.getAvatar(), 
    																		currentCreditBalance, creditAmount, transactionType, 
    																		transactionStatus, description, descriptionParams);
    					}
    					
    					// Recording the credit package owner table
    					if (orderCreditPackage.getCreditPackage().getCreditType() != CreditPackageType.UNIT) {
    						this.createCreditPackageOwnerEntity(orderCreditPackage);
    					}
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    		});
    		
		} catch (Exception e) {
			success = false;
		}
    	
    	return new ResponseEntity<Object>(Collections.singletonMap("success", success), HttpStatus.OK);
    }
    
    private void createCreditPackageOwnerEntity(OrderCreditEntryEntity creditOrder) {
		try {
			CreditPackageEntity creditPackage = creditOrder.getCreditPackage();
			int numberOfExpiredDay = creditPackage.getNumberOfExpiredDay();
			LocalDateTime expiredDate = LocalDateTime.now();
			expiredDate = expiredDate.plusDays(numberOfExpiredDay + 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
			
			UserCreditPackageEntity entity = new UserCreditPackageEntity();
			entity.setExpiredDate(expiredDate);
			entity.setOrder(creditOrder.getOrder());
			entity.setOwnerUuid(creditOrder.getOwnerUuid());
			entity.setTotalCredit(creditPackage.getCredit() + creditPackage.getBonusCredit());
			creditPackageOwnerRepo.save(entity);
		} catch (Exception e) {
			LOG.error("Could not create credit package owner with exception: {}", e.getMessage());
		}
	}
}
