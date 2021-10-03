package asia.cmg.f8.commerce.facade;

import asia.cmg.f8.commerce.constants.PaymentType;
import asia.cmg.f8.commerce.dto.PaymentTransactionType;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.PaymentTransactionEntity;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.commerce.service.QueryPaymentService;
import asia.cmg.f8.commerce.utils.PaymentUtils;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.inject.Inject;

@Component
public class PaymentFacade {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentFacade.class);

   // @Inject
    private final QueryPaymentService queryPaymentService;
    
    //@Inject
    private final OrderService orderService;
    
    @Inject
    public PaymentFacade(final QueryPaymentService queryPaymentService, final OrderService orderService){
    
    	this.queryPaymentService = queryPaymentService;
    	this.orderService = orderService;
    	
    }
    
    public boolean queryPaymentStatus(final String orderUuid) {
        final OrderEntity order = orderService.getOrderWithTransactions(orderUuid);
        if (order == null) {
            LOG.info("Order not found {}", orderUuid);
            return false;
        }

        if (PaymentUtils.isOrderFinish(order)) {
            LOG.info("Can't query payment for this order {} with payment status {}", orderUuid,
                    order.getPaymentStatus());
            return false;
        }

        final Optional<PaymentTransactionEntity> transactionOpt = order.getPaymentTransactions().stream()
                .findFirst()
                .filter(trans -> PaymentTransactionType.PAY.equals(trans.getTransactionType()));
        if (!transactionOpt.isPresent()) {
            LOG.info("Transaction is missing for order {}", orderUuid);
            return false;
        }

        final PaymentTransactionEntity payTransaction = transactionOpt.get();
        final String transactionRef = payTransaction.getRequestId();
        final PaymentType paymentType = payTransaction.getPaymentType();

        final String requestData = queryPaymentService.createQueryRequestTransaction(order,
                transactionRef, paymentType);
        if (StringUtils.isEmpty(requestData)) {
            return false;
        }

        return queryPaymentService.queryPaymentStatus(order, paymentType, requestData,
                transactionRef);
    }
}
