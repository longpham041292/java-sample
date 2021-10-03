package asia.cmg.f8.commerce.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.config.PaymentProperties.PaymentModule;
import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.constants.PaymentType;
import asia.cmg.f8.commerce.constants.TransactionCommand;
import asia.cmg.f8.commerce.constants.TransactionResponseCode;
import asia.cmg.f8.commerce.dto.PaymentTransactionEntryType;
import asia.cmg.f8.commerce.dto.PaymentTransactionType;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.PaymentTransactionEntity;
import asia.cmg.f8.commerce.entity.PaymentTransactionEntryEntity;
import asia.cmg.f8.commerce.utils.PaymentUtils;
import asia.cmg.f8.commerce.utils.QueryPaymentUtils;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;

@Service
public class QueryPaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(QueryPaymentService.class);
    
    private final OrderService orderService;
    
    private final CommerceEventService commerceEventService;
    
    private final PaymentProperties paymentProps;
    
    private final RestTemplate restTemplate;
    
    
    @Inject
    public QueryPaymentService(final OrderService orderService, final CommerceEventService commerceEventService, final PaymentProperties paymentProps, final RestTemplateBuilder builder){
    	this.orderService = orderService;
    	this.commerceEventService = commerceEventService;
    	this.paymentProps = paymentProps;
    	this.restTemplate = builder.build();
    }
    
    /**
     * Create new transaction for query. New transaction entry for that transaction.
     * @param order order
     * @param transactionRef transaction number
     * @param paymentType domestic or international
     * @return request data used to make a post request
     */
    @Transactional
    public String createQueryRequestTransaction(final OrderEntity order,
            final String transactionRef, final PaymentType paymentType) {
        String requestData;
        PaymentModule paymentModule;
        if (PaymentUtils.isDomesticPayment(paymentType)) {
            paymentModule = paymentProps.getDomestic();
            requestData = buildQueryDomesticRequestData(transactionRef);
        } else {
            paymentModule = paymentProps.getInternational();
            requestData = buildQueryInternationalRequestData(transactionRef);
        }
        
        if (StringUtils.isEmpty(requestData)) {
            LOG.info("Error when builder request data for order uuid {}", order.getUuid());
            return null;
        }
        
        final LocalDateTime now = LocalDateTime.now();
        final Double amount = PaymentUtils.getAmount(paymentProps, paymentModule,
                order.getTotalPrice());
        final PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setPaymentProvider(PaymentConstant.PAYMENT_PROVIDER);
        transaction.setCurrency(order.getCurrency());
        transaction.setAmount(amount);
        transaction.setCreatedDate(now);
        transaction.setRequestId(transactionRef);
        transaction.setTransactionType(PaymentTransactionType.QUERY);
        transaction.setPaymentType(paymentType);

        final PaymentTransactionEntryEntity transactionEntry = new PaymentTransactionEntryEntity();
        transactionEntry.setAmount(amount);
        transactionEntry.setCurrency(order.getCurrency());
        transactionEntry.setRequestId(transactionRef);

        transactionEntry.setCreatedDate(now);
        transactionEntry.setTransactionDate(now);
        transactionEntry.setTransactionType(PaymentTransactionEntryType.REQUEST);
        transactionEntry.setRequestData(requestData);
        transactionEntry.setVersionId(paymentProps.getQueryVersionModule());

        transaction.addTransactionEntry(transactionEntry);
        order.addPaymentTransaction(transaction);

        orderService.save(order);
        return requestData;
    }
    
    /**
     * Call query payment.
     * @param order order
     * @param paymentType payment type
     * @param requestData request data
     * @param transactionRef transaction id
     * @return result of query
     */
    @Transactional
    public boolean queryPaymentStatus(final OrderEntity order, final PaymentType paymentType,
            final String requestData, final String transactionRef) {
        String resUrl = paymentProps.getInternational().getStatusQueryUrl();
        if (PaymentUtils.isDomesticPayment(paymentType)) {
        	resUrl = paymentProps.getDomestic().getStatusQueryUrl();
        } 
        
        resUrl += "?" + requestData;
    	final String resResponse = restTemplate.postForObject(resUrl, requestData, String.class);
    	
        if (StringUtils.isEmpty(resResponse)) {
            LOG.error("Error when query payment of order {} ", order.getUuid());
            return false;
        }
        final Map<String, String> responseFields = QueryPaymentUtils.createMapFromResponse(resResponse);
        return handleQueryResponseData(order, responseFields, resResponse, transactionRef);
    }
    
    private boolean handleQueryResponseData(final OrderEntity order,
            final Map<String, String> responseFields, final String resQs,
            final String transactionRef) {
        final String drExists = responseFields.get(PaymentConstant.VCP_DREXISTS);
        final String txnResponseCode = responseFields
                .get(PaymentConstant.VCP_TRANSACTION_RESPONSE_CODE);
        final String transactionNo = responseFields.get(PaymentConstant.VCP_TRANSACTION_NO);
        final String amount = responseFields.get(PaymentConstant.VCP_AMOUNT);
        final String message = responseFields.get(PaymentConstant.VCP_MESSAGE);
        final String version = responseFields.get(PaymentConstant.VCP_VERSION);

        LOG.info("Transaction exist? {}", drExists);
        LOG.info("Transaction response code {}", txnResponseCode);

        final LocalDateTime now = LocalDateTime.now();

        // add get latest Query transaction by createdDate
        final PaymentTransactionEntity transaction = order
                .getPaymentTransactions()
                .stream()
                .filter(trans -> PaymentTransactionType.QUERY.equals(trans.getTransactionType()))
                .max((trans1, trans2) -> trans1.getCreatedDate().compareTo(trans2.getCreatedDate()))
                .get();

        final PaymentTransactionEntryEntity transactionEntry = new PaymentTransactionEntryEntity();
        if (!(StringUtils.isEmpty(amount) || PaymentConstant.NO_VALUE_RETURN.equals(amount))) {
            transactionEntry.setAmount(PaymentUtils.getRealAmount(amount));
        }
        transactionEntry.setCurrency(order.getCurrency());
        transactionEntry.setRequestId(transactionRef);

        transactionEntry.setCreatedDate(now);
        transactionEntry.setTransactionDate(now);
        transactionEntry.setTransactionType(PaymentTransactionEntryType.RESPONSE);
        transactionEntry.setRequestData(resQs);
        transactionEntry.setVersionId(version);
        transactionEntry.setTransactionRespCode(txnResponseCode);
        transactionEntry.setTransactionNo(transactionNo);
        transactionEntry.setTransactionStatus(txnResponseCode);
        transactionEntry.setTransactionStatusDetail(message);

        transaction.addTransactionEntry(transactionEntry);

        if (PaymentConstant.TRANSACTION_EXIST.equals(drExists)
                && TransactionResponseCode.SUCCESS.getCode().equals(txnResponseCode)) {
            LOG.info("Query payment with result success. Complete this order: {}", order.getUuid());
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.COMPLETED);
            orderService.save(order);
            commerceEventService.publishOrderCompleteEvent(order.getUuid());
        } else {
            LOG.info("Query payment with result fail. Cancel this order: {}", order.getUuid());
            order.setPaymentStatus(PaymentStatus.FAIL);
            order.setOrderStatus(OrderStatus.CANCELLED);
            orderService.save(order);
        }

        return true;
    }
    
    private String buildQueryDomesticRequestData(final String transactionRef) { 
        final Map<String, Object> requestFields = new HashMap<>();
        final PaymentModule domestic = paymentProps.getDomestic();
        requestFields.put(PaymentConstant.VCP_COMMAND, TransactionCommand.QUERY.getCode());
        requestFields.put(PaymentConstant.VCP_VERSION, paymentProps.getQueryVersionModule());
        requestFields.put(PaymentConstant.VCP_MERCHANT_TRANSACTION_REF, transactionRef);
        requestFields.put(PaymentConstant.VCP_MERCHANT, domestic.getMerchantId());
        requestFields.put(PaymentConstant.VCP_ACCESS_CODE, domestic.getAccessCode());
        
        requestFields.put(PaymentConstant.VCP_USER, paymentProps.getPaymentUser());
        requestFields.put(PaymentConstant.VCP_PWD, paymentProps.getPaymentPassword());
        
        return QueryPaymentUtils.createPostDataFromMap(requestFields);
    }
    
    private String buildQueryInternationalRequestData(final String transactionRef) {
        final Map<String, Object> requestFields = new HashMap<>();
        final PaymentModule international = paymentProps.getInternational();
        requestFields.put(PaymentConstant.VCP_COMMAND, TransactionCommand.QUERY.getCode());
        requestFields.put(PaymentConstant.VCP_VERSION, paymentProps.getQueryVersionModule());
        requestFields.put(PaymentConstant.VCP_MERCHANT_TRANSACTION_REF, transactionRef);
        requestFields.put(PaymentConstant.VCP_MERCHANT, international.getMerchantId());
        requestFields.put(PaymentConstant.VCP_ACCESS_CODE, international.getAccessCode());
        
        requestFields.put(PaymentConstant.VCP_USER, paymentProps.getPaymentUser());
        requestFields.put(PaymentConstant.VCP_PWD, paymentProps.getPaymentPassword());
        
        return QueryPaymentUtils.createPostDataFromMap(requestFields);
    }
    
}
