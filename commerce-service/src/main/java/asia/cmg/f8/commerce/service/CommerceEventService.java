package asia.cmg.f8.commerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.inject.Inject;

import asia.cmg.f8.commerce.WalletEvent;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.commerce.api.OrderApi;
import asia.cmg.f8.commerce.dto.Order;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderEntryEntity;
import asia.cmg.f8.commerce.entity.OrderSubscriptionEntryEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.SubscriptionEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;
import asia.cmg.f8.commerce.event.CommerceEventProducer;
import asia.cmg.f8.common.event.OrderCreditCompletedEvent;
import asia.cmg.f8.common.event.OrderSubscriptionCompletedEvent;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;

@Service
public class CommerceEventService {

	private static final Logger LOG = LoggerFactory.getLogger(CommerceEventService.class);
	
    //@Inject
    private final CommerceEventProducer eventProducer;

    //@Inject
    private final OrderService orderService;
    
    @Inject
    public CommerceEventService(final CommerceEventProducer eventProducer, OrderService orderService){
    	
    	this.eventProducer = eventProducer;
    	this.orderService = orderService;
    }
    

    /**
     * Publish order complete event.
     * @param orderUuid Order Uuid
     */
    public void publishOrderCompleteEvent(final String orderUuid) {
        final OrderEntity order = orderService.getOrder(orderUuid);
        if(OrderType.SUBSCRIPTION.name().compareToIgnoreCase(order.getType()) == 0) {
        	        	
        	final OrderSubscriptionEntryEntity entity = order.getOrderSubscriptionEntries().get(0);
        	
        	final SubscriptionEntity supscription = entity.getSubscription();
        	
        	Calendar date = GregorianCalendar.getInstance();
        	date.add(Calendar.DAY_OF_MONTH, supscription.getLimitDay());
    		
        	
        	final  OrderSubscriptionCompletedEvent event = OrderSubscriptionCompletedEvent.newBuilder()
        			.setOrderUuid(order.getUuid())
        			.setSubscriptionId(supscription.getId())
        			.setEuUuid(order.getUserUuid()).setPtUuid(order.getPtUuid())
        			.setLimitDay(supscription.getLimitDay())
        			.setNumberOfMonth(supscription.getNumberOfMonth())
        			.setStartTime(System.currentTimeMillis())
        			.setPtUuid(order.getPtUuid())
        			.setEndTime(date.getTimeInMillis())
        			.build();
        			
        	LOG.info("publishOrderCompleteEvent -> Order Subscription : {}",event.toString());
        	eventProducer.publish(event);
        }
        else if(OrderType.PRODUCT.name().compareToIgnoreCase(order.getType()) == 0) {
        	final OrderEntryEntity orderEntry = order.getOrderProductEntries().get(0);
            final ProductEntity product = orderEntry.getProduct();
            final OrderCompletedEvent event = OrderCompletedEvent.newBuilder()
                    .setEventId(UUID.randomUUID().toString()).setOrderId(order.getUuid())
                    .setOrderCode(order.getCode())
                    .setCreatedDate(ZoneDateTimeUtils.convertToSecondUTC(order.getCreatedDate()))
                    .setNumberOfSession(orderEntry.getQuantity())
                    .setExpireLimit(orderEntry.getExpireLimit())
                    .setPackagePrice(order.getTotalPrice())
                    .setOriginalPrice(order.getSubTotal())
                    .setDiscount(order.getDiscount())
                    .setCommission(orderEntry.getCommision()).setCurrency(order.getCurrency())
                    .setUserUuid(order.getUserUuid()).setPtUuid(order.getPtUuid())
                    .setProductUuid(product.getUuid()).setProductName(product.getLevel().getCode())
                    .setSubmittedAt(System.currentTimeMillis()).setFreeOrder(order.getFreeOrder())
                    .setContractNumber(order.getContractNumber())
                    .setClubcode(order.getClubcode())
                    .setTrainingStyle(product.getTrainingStyle().name())
                    .build();
            LOG.info("publishOrderCompleteEvent -> Order Product : {}",event.toString());
            eventProducer.publish(event);
        }
        else if(OrderType.CREDIT.name().equalsIgnoreCase(order.getType())) {
        	final OrderCreditEntryEntity orderCreditEntry = order.getOrderCreditPackageEntries().get(0);
        	final CreditPackageEntity creditPackage = orderCreditEntry.getCreditPackage();
        	
        	final OrderCreditCompletedEvent event = OrderCreditCompletedEvent.newBuilder()
        			.setCredit(creditPackage.getCredit() + creditPackage.getBonusCredit())
        			.setCreditPackageTye(creditPackage.getCreditType().name())
        			.setLimitDay(creditPackage.getNumberOfExpiredDay())
        			.setOrderId(order.getId())
        			.setOwnerUuid(orderCreditEntry.getOwnerUuid())
        			.build();
        	
        	LOG.info("publishOrderCreditCompleteEvent -> Order Credit : {}", event.toString());
            eventProducer.publish(event);
        }
    }

    public void publishPurchasedPackageEvent(OrderCreditEntryEntity orderCredit) {
    	try {
			WalletEvent event = WalletEvent.newBuilder()
					.setEventId(UUID.randomUUID().toString())
					.setNotiType("PURCHASE_PACKAGE")
					.setPackageName(orderCredit.getCreditPackage().getCreditType().name())
					.setUserUuid(orderCredit.getOrder().getUserUuid())
					.build();
			eventProducer.publish(event);
		} catch (Exception ex) {
    		LOG.error("publishPurchasedPackageEvent: {}", ex);
		}
	}

    public void publishUpgradedWalletEvent(CreditWalletLevel oldLevel, CreditWalletEntity creditWalletEntity) {
    	try {
			WalletEvent event = WalletEvent.newBuilder()
					.setEventId(UUID.randomUUID().toString())
					.setNotiType("UPGRADE_WALLET")
					.setUserUuid(creditWalletEntity.getOwnerUuid())
					.setPackageName(creditWalletEntity.getLevel().name())
					.setOldPackageName(oldLevel.name())
					.build();
			eventProducer.publish(event);
		} catch (Exception ex) {
			LOG.error("publishUpgradedWalletEvent: {}", ex);
		}
	}
    
    public Boolean publishNotificationExpiredCreditPackageEvent(OrderCreditEntryEntity orderCredit) {
    	try {
    		WalletEvent event = WalletEvent.newBuilder()
    				.setEventId(UUID.randomUUID().toString())
    				.setNotiType("EXPIRE_PACKAGE")
    				.setPackageName(orderCredit.getCreditPackage().getCreditType().name())
    				.setUserUuid(orderCredit.getOrder().getUserUuid())
    				.build();
    		eventProducer.publish(event);
    		return Boolean.TRUE;
		} catch (Exception ex) {
			LOG.error("publishNotificationExpiredCreditPackageEvent: {}", ex);
			return Boolean.FALSE;
		}
    }
}
