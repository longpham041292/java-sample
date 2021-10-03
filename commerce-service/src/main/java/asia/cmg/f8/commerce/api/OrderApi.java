package asia.cmg.f8.commerce.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import asia.cmg.f8.commerce.client.SessionClient;
import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.constants.CommerceErrorConstantCode;
import asia.cmg.f8.commerce.dto.CreateOrderCodResponse;
import asia.cmg.f8.commerce.dto.CreateOrderCreditDTO;
import asia.cmg.f8.commerce.dto.CreateOrderProductDto;
import asia.cmg.f8.commerce.dto.CreateOrderResponse;
import asia.cmg.f8.commerce.dto.CreateOrderSubscriptionDto;
import asia.cmg.f8.commerce.dto.CrmContractDto;
import asia.cmg.f8.commerce.dto.EditOrderDto;
import asia.cmg.f8.commerce.dto.FreeOrderRequest;
import asia.cmg.f8.commerce.dto.Order;
import asia.cmg.f8.commerce.dto.OrderCreditsAmountRequest;
import asia.cmg.f8.commerce.dto.OrderHistory;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.exception.CreditCouponException;
import asia.cmg.f8.commerce.facade.OrderFacade;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.commerce.service.PromotionService;
import asia.cmg.f8.commerce.service.UserService;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.context.LanguageContext;
import asia.cmg.f8.common.dto.ApiRespObject;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.security.annotation.RequiredAdminRole;
import asia.cmg.f8.common.util.CommonConstant;
import asia.cmg.f8.common.web.errorcode.ErrorCode;

@RestController
public class OrderApi {

	private final OrderFacade orderFacade;
	private final PaymentProperties paymentProps;
	private final SessionClient sessionClient;
	private final UserService userService;

	@Autowired
	private PromotionService promotionService;

	@Autowired
	private OrderService orderService;

	private static final String UUID = "uuid";
	private static final Integer SUCCESS = 1, FAIL = 0;
	private static final String COUPON_CODE = "coupon_code";

	private static final Logger LOG = LoggerFactory.getLogger(OrderApi.class);

	@Autowired
	public OrderApi(final OrderFacade orderFacade, final PaymentProperties paymentProps, SessionClient sessionClient,
			UserService userService) {
		this.orderFacade = orderFacade;
		this.paymentProps = paymentProps;
		this.sessionClient = sessionClient;
		this.userService = userService;
	}

	@RequestMapping(value = "/orders", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> createOrder(@RequestBody @Valid final CreateOrderProductDto orderDto,
			final Account account) {
		if (!orderDto.isAcceptTerm()) {
			return new ResponseEntity<>(Collections.singletonMap(CommerceUtils.CONTRACT, Boolean.FALSE),
					HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(orderFacade.processOrderProduct(orderDto, account, false, null), HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/products", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> createOrderProduct(@RequestBody @Valid final CreateOrderProductDto orderDto,
			final Account account) {
		return createOrder(orderDto, account);
	}

	@RequestMapping(value = "/orders/subscriptions", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> createOrderSubscriptions(@RequestBody @Valid final CreateOrderSubscriptionDto orderDto,
			final Account account) {
		if (!orderDto.isAcceptTerm()) {
			return new ResponseEntity<>(Collections.singletonMap(CommerceUtils.CONTRACT, Boolean.FALSE),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(orderFacade.processOrderSubscription(orderDto, account), HttpStatus.OK);
	}

	/**
	 * Order credit package
	 * 
	 * @param orderDTO
	 * @param account
	 * @return
	 */
	@PostMapping(value = "/orders/wallets/credits", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> createOrderCreditPackage(@RequestBody @Valid final CreateOrderCreditDTO orderDTO,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!orderDTO.isAcceptTerm()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Bad request"));
			} else {
				CreateOrderResponse orderResponse = orderFacade.processOrderCreditPackage(orderDTO, account);
				apiResponse.setData(orderResponse);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	/**
	 * Order amount of credits
	 * 
	 * @param account
	 * @return
	 */
	@PostMapping(value = "/mobile/v1/orders/wallets/credits-amount", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> createOrderCreditsAmount(@RequestBody @Valid OrderCreditsAmountRequest orderRequest,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);

		try {
			if (!orderRequest.isAcceptTerm()) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail("Bad request"));
			} else {
				CreateOrderResponse orderResponse = orderFacade.processOrderCreditsAmount(orderRequest, account);
				apiResponse.setData(orderResponse);
			}
		} catch (Exception e) {
			apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
		}

		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/promotion/{uuid}/{couponCode}", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> createOrderWithCouponCode(
			@PathVariable(name = "couponCode", required = true) final String couponCode,
			@PathVariable(UUID) final String ptUuid, @RequestBody @Valid final CreateOrderProductDto orderDto,
			final Account account) {
		if (!orderDto.isAcceptTerm()) {
			return new ResponseEntity<>(Collections.singletonMap(CommerceUtils.CONTRACT, Boolean.FALSE),
					HttpStatus.BAD_REQUEST);
		}

		final int promotionCode = promotionService.getPromotionCode(couponCode, account.uuid(), ptUuid, paymentProps);
		if (CommerceErrorConstantCode.COUPON_CODE_VALID != promotionCode) {
			return new ResponseEntity<>(new ErrorCode(promotionCode, "INVALID_COUPON_CODE", "Invalid coupon code"),
					HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(orderFacade.processOrderProduct(orderDto, account, false, couponCode),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/confirm", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> forceCreateNewOrder(@RequestBody @Valid final CreateOrderProductDto orderDto,
			final Account account) {
		if (!orderDto.isAcceptTerm()) {
			return new ResponseEntity<>(Collections.singletonMap(CommerceUtils.CONTRACT, Boolean.FALSE),
					HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(orderFacade.processOrderProduct(orderDto, account, true, null), HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/confirm/promotion/{uuid}/{couponCode}", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<?> forceCreateNewOrder(
			@PathVariable(name = "couponCode", required = true) final String couponCode,
			@PathVariable(UUID) final String ptUuid, @RequestBody @Valid final CreateOrderProductDto orderDto,
			final Account account) {
		if (!orderDto.isAcceptTerm()) {
			return new ResponseEntity<>(Collections.singletonMap(CommerceUtils.CONTRACT, Boolean.FALSE),
					HttpStatus.BAD_REQUEST);
		}

		final int promotionCode = promotionService.getPromotionCode(couponCode, account.uuid(), ptUuid, paymentProps);
		if (CommerceErrorConstantCode.COUPON_CODE_VALID != promotionCode) {
			return new ResponseEntity<>(new ErrorCode(promotionCode, "INVALID_COUPON_CODE", "Invalid coupon code"),
					HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(orderFacade.processOrderProduct(orderDto, account, true, couponCode),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Order> getOrder(@PathVariable(name = "uuid", required = true) final String orderUuid,
			final Account account, final LanguageContext language) {
		return new ResponseEntity<>(orderFacade.getOrderByUuid(orderUuid, account, language.language()), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/orders/{uuid}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<Order> getOrderByAdmin(@PathVariable(name = "uuid", required = true) final String orderUuid,
			final LanguageContext language) {
		return new ResponseEntity<>(orderFacade.getOrderByUuid(orderUuid, language.language()), HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/orders", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> updateOrder(@RequestBody @Valid final EditOrderDto orderDto) {
		final Map<String, String> resp = new HashMap<>();
		LocalDate expiredDate = CommerceUtils.parseDate(orderDto.getExpiredDate());

		if (expiredDate.isBefore(LocalDate.now())) {
			resp.put("error", "expired date must not less then current date");
			return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
		}
		orderService.updateOrderExpiredDate(expiredDate, orderDto.getUuid());
		sessionClient.cancelInvalidSessions(orderDto.getUuid(), orderDto.getExpiredDate());
		resp.put("uuid", orderDto.getUuid());
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@RequestMapping(value = "/orders", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<OrderHistory>> getAllOrders(final Account account, final LanguageContext language) {
		return new ResponseEntity<>(orderFacade.getOrderByUser(account.uuid(), language.language()), HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/free", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> createFreeOrder(@RequestBody @Valid final FreeOrderRequest order, final Account account) {
		LOG.info("Request free order info {}", order.toString());
		if (StringUtils.isEmpty(order.getContractNumber())) {
			return new ResponseEntity<>("contract number must not empty", HttpStatus.BAD_REQUEST);
		}
		final Map<String, String> resp = new HashMap<>();
		resp.put("order_uuid", orderFacade.createFreeOrder(order, null));

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/crmcontract", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> createCrmContract(@RequestBody @Valid final CrmContractDto contract,
			final Account account) {
		LOG.info("Crm contract info {}", contract);
		final BasicUserEntity eu = userService.getOneByUserCode(contract.getEuCode());
		if (eu == null || !CommonConstant.EU_USER_TYPE.equalsIgnoreCase(eu.getUserType())) {
			return new ResponseEntity<>("Can not find eu code: " + contract.getEuCode(), HttpStatus.BAD_REQUEST);
		}
		final BasicUserEntity pt = userService.getOneByUserCode(contract.getPtCode());
		if (pt == null || !CommonConstant.PT_USER_TYPE.equalsIgnoreCase(pt.getUserType())) {
			return new ResponseEntity<>("Can not find pt code: " + contract.getPtCode(), HttpStatus.BAD_REQUEST);
		}

		final Map<String, String> resp = new HashMap<>();

		resp.put("order_uuid", orderFacade.createCrmContract(contract, pt, eu));

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@RequestMapping(value = "/orders/freeForMigrationUser", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public Integer createFreeOrderMigrationUsers(@RequestBody @Valid final List<FreeOrderRequest> orders) {

		LOG.info("----------Invoking /orders/freeForMigrationUser order size {}", orders.size());

		return orderFacade.createFreeOrders(orders).size() > 0 ? SUCCESS : FAIL;
	}

	@RequestMapping(value = "/orders/free/promotion/{uuid}/{couponCode}", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	@RequiredAdminRole
	public ResponseEntity<?> createFreeOrder(
			@PathVariable(name = "couponCode", required = true) final String couponCode,
			@PathVariable(UUID) final String ptUuid, @RequestBody @Valid final FreeOrderRequest order,
			final Account account) {

		final int promotionCode = promotionService.getPromotionCode(couponCode, account.uuid(), ptUuid, paymentProps);
		if (CommerceErrorConstantCode.COUPON_CODE_VALID != promotionCode) {
			return new ResponseEntity<>(new ErrorCode(promotionCode, "INVALID_COUPON_CODE", "Invalid coupon code"),
					HttpStatus.BAD_REQUEST);
		}

		LOG.info("Request free order info {}", order.toString());
		final Map<String, String> resp = new HashMap<>();
		resp.put("order_uuid", orderFacade.createFreeOrder(order, couponCode));

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@PostMapping(value = "/orders/wallets/credits/coupons", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> createOrderCreditsCoupons(@RequestBody Map<String, String> couponCode,
			final Account account) {
		ApiRespObject<Object> apiResponse = new ApiRespObject<Object>();
		apiResponse.setStatus(ErrorCode.SUCCESS);
		String code = couponCode.get(COUPON_CODE);
		if (code == null) {
			apiResponse.setStatus(CommerceErrorConstantCode.COUPON_INVALID);
		} else {
			try {
				CreateOrderCodResponse orderResponse = orderFacade.processOrderCreditsCoupons(code, account);
				apiResponse.setData(orderResponse);
			} catch (CreditCouponException ex) {
				apiResponse.setStatus(ex.getErrorCode());
			} catch (Exception e) {
				apiResponse.setStatus(ErrorCode.FAILED.withDetail(e.getMessage()));
			}
		}
		return new ResponseEntity<Object>(apiResponse, HttpStatus.OK);
	}
	
	@GetMapping(value = "/mobile/v1/orders/wallets/credits/users/check-success-ordered", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkSuccessOrdered(final Account account) {
		Boolean successOrdered = false;
		try {
			successOrdered =  orderService.checkSuccessOrdered(account.uuid());
		} catch (Exception e) {
			LOG.error("[checkSuccessOrdered] error detail: {}", e.getMessage());
			return new ResponseEntity<Object>(Collections.singletonMap("success_ordered", false) , HttpStatus.OK);
		}
		return new ResponseEntity<Object>(Collections.singletonMap("success_ordered", successOrdered) , HttpStatus.OK);
	}
}
