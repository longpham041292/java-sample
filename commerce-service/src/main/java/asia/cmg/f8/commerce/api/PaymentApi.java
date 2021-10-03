package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.constants.TransactionStatus;
import asia.cmg.f8.commerce.dto.InstrumentInfoDto;
import asia.cmg.f8.commerce.dto.TransactionStatusInfo;
import asia.cmg.f8.commerce.service.MessageService;
import asia.cmg.f8.commerce.service.OnepayInstrumentService;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.commerce.service.PaymentService;
import asia.cmg.f8.commerce.service.UserService;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.security.Account;


@RestController
public class PaymentApi {

	private static final String TEXT_HTML_VALUE_UTF8 = "text/html;charset=UTF-8";
	
    private static final Logger LOG = LoggerFactory.getLogger(PaymentApi.class);
    
    private final PaymentService paymentService;
    
    private final OrderService orderService;
    
    private final PaymentProperties paymentProps;
    
    private final OnepayInstrumentService onepayInstrumentService;
    
    private final MessageService messageService;
    
    private final UserService userService;
    
    @Inject
    public PaymentApi(final PaymentService paymentService, 
    				final OrderService orderService, 
    				final PaymentProperties paymentProps,
    				final OnepayInstrumentService onepayInstrumentService,
    				final MessageService messageService,
    				final UserService userService){
    
    	this.paymentService = paymentService;
    	this.orderService = orderService;
    	this.paymentProps = paymentProps;
    	this.onepayInstrumentService = onepayInstrumentService;
		this.messageService = messageService;
    	this.userService = userService;
    }
    
    @RequestMapping(value = "/payment/return/domestic", method = RequestMethod.GET,
            produces = TEXT_HTML_VALUE_UTF8)
    public String processDomesticFrontReturnData(final HttpServletRequest request) {
        final Map<String, Object> responseFields = getRequestParams(request);

        LOG.info("Receive GET domestic payment response for order: {}",
                responseFields.get(PaymentConstant.VCP_ORDER_INFO));
        Boolean isDomestic = true;
        final TransactionStatusInfo transactionStatusInfo = paymentService
                .processResponseData(responseFields, isDomestic);
        
        return getHtmlPaymentResult(CommerceUtils.PAYMENT_RESPONSE_DOMESTIC_CODE_MESSAGE_PREFIX, transactionStatusInfo);
    }
    
    @RequestMapping(value = "/payment/return/domestic", method = RequestMethod.POST)
    public String processDomesticBackReturnData(final HttpServletRequest request) {
        final Map<String, Object> responseFields = getRequestParams(request);

        LOG.info("Receive POST domestic payment response for order: {}",
                responseFields.get(PaymentConstant.VCP_ORDER_INFO));
        Boolean isDomestic = true;
        // TODO add more logic
        final TransactionStatusInfo transactionStatusInfo = paymentService
                .processResponseData(responseFields, isDomestic);
        		return returnBEResponseCode(transactionStatusInfo);
    }
    
    @RequestMapping(value = "/payment/return/international", method = RequestMethod.GET,
            produces = TEXT_HTML_VALUE_UTF8)
    public String processInternationalFrontReturnData(final HttpServletRequest request) {

    	final Map<String, Object> responseFields = getRequestParams(request);
        
        LOG.info("Receive GET International payment response for order: {}",
                responseFields.get(PaymentConstant.VCP_ORDER_INFO));
        Boolean isDomestic = false;

        final TransactionStatusInfo transactionStatusInfo = paymentService
                .processResponseData(responseFields, isDomestic);

		return getHtmlPaymentResult(CommerceUtils.PAYMENT_RESPONSE_INTERNATIONAL_CODE_MESSAGE_PREFIX, transactionStatusInfo);
    }
    
    @RequestMapping(value = "/payment/return/international", method = RequestMethod.POST)
    public String processInternationalBackReturnData(final HttpServletRequest request) {
        final Map<String, Object> responseFields = getRequestParams(request);

        LOG.info("Receive POST International payment response for order: {}",
                responseFields.get(PaymentConstant.VCP_ORDER_INFO));
        Boolean isDomestic = false;

        // TODO add more logic
        final TransactionStatusInfo transactionStatusInfo = paymentService
                .processResponseData(responseFields, isDomestic);
        
        return returnBEResponseCode(transactionStatusInfo);
    }
    
    @RequestMapping(value = "/orders/{orderUuid}/status", method = RequestMethod.GET,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> checkOrderStatus(
            @PathVariable("orderUuid") final String orderUuid, final Account account) {
        final String status = orderService.getOrderPaymentStatus(orderUuid, account.uuid());
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(CommerceUtils.RESULT, status);
        result.put("verify_phone", userService.checkVerifyPhone(account.uuid()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

	@RequestMapping(value = "/payment/instruments/list", method = RequestMethod.GET)
	public ResponseEntity<List<InstrumentInfoDto>> getAllInstrumentsInfo(
			@RequestParam(value = "pspId", required = false) final String pspId, final Account account) {
		if (StringUtils.isEmpty(pspId)) {
			return new ResponseEntity<List<InstrumentInfoDto>>(
					onepayInstrumentService.findAllInstruments(account.uuid()), HttpStatus.OK);
		}
		return new ResponseEntity<List<InstrumentInfoDto>>(
				onepayInstrumentService.findAllInstruments(account.uuid(), pspId), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/payment/instruments/{instrumentId}", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeInstrument(final Account account,
			@PathVariable("instrumentId") final Long instrumentId) {
		final TransactionStatus status = paymentService.removeOnepayInstrument(instrumentId, account);
		return new ResponseEntity<String>(status.toString(), HttpStatus.OK);
	}
    
    private Map<String, Object> getRequestParams(final HttpServletRequest request) {
        final Map<String, Object> responseFields = new HashMap<>();
        for (final Enumeration<String> param = request.getParameterNames(); param.hasMoreElements();) {
            final String fieldName = param.nextElement();
            final String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                responseFields.put(fieldName, fieldValue);
            }
        }
        return responseFields;
    }
    
    private String returnBEResponseCode(final TransactionStatusInfo transactionStatusInfo) {
        String responseCode = PaymentConstant.BACKEND_SUCCESS_CODE;
        if (TransactionStatus.INVALID_HASH.equals(transactionStatusInfo.getStatus())) {
            responseCode = PaymentConstant.BACKEND_FAIL_CODE;
        }
        return String.format(PaymentConstant.CONFIRM_RESPONSE, responseCode);
    }
    
	private String getHtmlPaymentResult(final String responseCodePrefix, final TransactionStatusInfo transactionStatusInfo) {
		final String responseMessageCode = responseCodePrefix + transactionStatusInfo.getResponseCode();
		String responseMessage = messageService.getMessage(responseMessageCode,
				userService.getLocale(transactionStatusInfo.getUserUuid()), null);
		if(responseMessage == null) {
			responseMessage = "";
		}
		
		String paymentResultHtml = transactionStatusInfo.getStatus() == TransactionStatus.SUCCESS ? 
				paymentProps.getPaymentSuccessHtml() : paymentProps.getPaymentFailedHtml();
		
		paymentResultHtml = paymentResultHtml.replaceAll(CommerceUtils.PAYMENT_STATUS_HOLDER, transactionStatusInfo.getStatus().getCode());
		paymentResultHtml = paymentResultHtml.replaceAll(CommerceUtils.PAYMENT_RESPONSE_CODE_HOLDER, responseMessage);
		
		return paymentResultHtml;
	}
}
