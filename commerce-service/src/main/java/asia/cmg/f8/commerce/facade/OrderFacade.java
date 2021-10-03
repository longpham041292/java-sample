package asia.cmg.f8.commerce.facade;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.client.UserClient;
import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.constants.CommerceErrorConstantCode;
import asia.cmg.f8.commerce.dto.BasicUserInfo;
import asia.cmg.f8.commerce.dto.CreateOrderCodResponse;
import asia.cmg.f8.commerce.dto.CreateOrderCreditDTO;
import asia.cmg.f8.commerce.dto.CreateOrderProductDto;
import asia.cmg.f8.commerce.dto.CreateOrderResponse;
import asia.cmg.f8.commerce.dto.CreateOrderSubscriptionDto;
import asia.cmg.f8.commerce.dto.CrmContractDto;
import asia.cmg.f8.commerce.dto.FreeOrderRequest;
import asia.cmg.f8.commerce.dto.Order;
import asia.cmg.f8.commerce.dto.OrderCreditsAmountRequest;
import asia.cmg.f8.commerce.dto.OrderEntry;
import asia.cmg.f8.commerce.dto.OrderHistory;
import asia.cmg.f8.commerce.dto.Product;
import asia.cmg.f8.commerce.dto.UserEntity;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderEntryEntity;
import asia.cmg.f8.commerce.entity.OrderSubscriptionEntryEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.entity.ProductEntity;
import asia.cmg.f8.commerce.entity.ProductTypeEntity;
import asia.cmg.f8.commerce.entity.PromotionEntity;
import asia.cmg.f8.commerce.entity.SubscriptionEntity;
import asia.cmg.f8.commerce.entity.credit.CreditCouponEntity;
import asia.cmg.f8.commerce.entity.credit.CreditCouponStatus;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;
import asia.cmg.f8.commerce.entity.credit.OrderCouponEntryEntity;
import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.exception.CreditCouponException;
import asia.cmg.f8.commerce.exception.PendingOrderException;
import asia.cmg.f8.commerce.repository.CreditCouponsRepository;
import asia.cmg.f8.commerce.repository.PromotionRepository;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.service.CommerceEventService;
import asia.cmg.f8.commerce.service.CreditPackageSevice;
import asia.cmg.f8.commerce.service.CreditWalletTransactionService;
import asia.cmg.f8.commerce.service.OrderCodeGenerator;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.commerce.service.PaymentService;
import asia.cmg.f8.commerce.service.ProductService;
import asia.cmg.f8.commerce.service.ProductTypeLevelService;
import asia.cmg.f8.commerce.service.SubscriptionService;
import asia.cmg.f8.commerce.service.UserService;
import asia.cmg.f8.commerce.service.WalletService;
import asia.cmg.f8.commerce.utils.CommerceCouponUtils;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;
import asia.cmg.f8.common.util.AESEncryptUtils;
import asia.cmg.f8.common.util.CurrencyUtils;
import asia.cmg.f8.common.util.UserGridResponse;

@Component
@SuppressWarnings("PMD.ExcessiveImports")
public class OrderFacade {

	private static final Logger LOG = LoggerFactory.getLogger(OrderFacade.class);

	private final OrderService orderService;

	private final UserClient userClient;

	private final OrderCodeGenerator orderCodeGenerator;

	private final ProductService productService;

	private final SubscriptionService subscriptionService;

	private final ProductTypeLevelService typeLevelService;

	private final PaymentService paymentService;

	private final CommerceEventService commerceEventService;

	private final CommerceProperties commerceProps;

	@Autowired
	private CreditPackageSevice creditPackageService;

	@Inject
	public OrderFacade(final OrderService orderService, final UserClient userClient,
			final OrderCodeGenerator orderCodeGenerator, final ProductService productService,
			final ProductTypeLevelService typeLevelService, final PaymentService paymentService,
			final CommerceEventService commerceEventService, final CommerceProperties commerceProps,
			final SubscriptionService subscriptionService) {

		this.orderService = orderService;
		this.userClient = userClient;
		this.orderCodeGenerator = orderCodeGenerator;
		this.productService = productService;
		this.typeLevelService = typeLevelService;
		this.paymentService = paymentService;
		this.commerceEventService = commerceEventService;
		this.commerceProps = commerceProps;
		this.subscriptionService = subscriptionService;
	}

	@Autowired
	private PromotionRepository promotionRepository;

	@Autowired
	private CreditCouponsRepository creditCouponsRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private WalletService walletService;

	@Autowired
	private CreditWalletTransactionService walletTransactionService;

	@Autowired
	private UserCreditPackageRepository creditPackageOwnerRepo;

	/**
	 * Process Order Product
	 * 
	 * @param orderDto
	 * @param account
	 * @param confirmCancel
	 * @param couponCode
	 * @return
	 */
	@Transactional
	public CreateOrderResponse processOrderProduct(final CreateOrderProductDto orderDto, final Account account,
			final boolean confirmCancel, final String couponCode) {
		final ProductEntity product = productService.getProductIfValid(orderDto);
		if (product == null) {
			throw new IllegalArgumentException("Product is invalid");
		}

		final ProductTypeEntity productType = typeLevelService.getProductTypeIfValid(orderDto.getUnitPrice(),
				product.getLevel().getCode(), commerceProps.getCountry());
		if (productType == null) {
			throw new IllegalArgumentException("Product type is invalid");
		}

		if (orderService.hasPendingOrderWithPt(account.uuid(), orderDto.getPtUuid())) {
			if (confirmCancel) {
				LOG.info("Cancel pending order for user {} with pt {}", account.uuid(), orderDto.getPtUuid());
				orderService.cancelPendingOrdersWithPt(account.uuid(), orderDto.getPtUuid());
			} else {
				throw new PendingOrderException("A PENDING order with current PT is exist!");
			}
		}

		final UserGridResponse<UserEntity> ptResp = userClient.getUser(orderDto.getPtUuid());
		if (ptResp.getEntities().isEmpty()) {
			throw new IllegalArgumentException("PT user is invalid");
		}
		final UserEntity ptUser = ptResp.getEntities().get(0);
		if (!ptUser.getLevel().equalsIgnoreCase(product.getLevel().getCode())) {
			throw new IllegalArgumentException("Product and PT level are mismatch");
		}

		String contactNumber = "";
		final String orderUuid = createOrderProduct(product, productType, ptUser, account.uuid(), couponCode, false,
				contactNumber);

		String url = StringUtils.EMPTY;
		if (orderDto.getInstrumentId() == null) {
			url = paymentService.processPaymentRequest(orderUuid, orderDto.getPaymentType(), account.language());
		} else {
			url = paymentService.handleInstrumentPayment(orderUuid, orderDto.getInstrumentId(), account);
		}

		return new CreateOrderResponse(orderUuid, url);
	}

	/**
	 * Process Order Subscription
	 *
	 * @param orderDto
	 * @param account
	 * @return
	 */
	@Transactional
	public CreateOrderResponse processOrderSubscription(final CreateOrderSubscriptionDto orderDto,
			final Account account) {
		final SubscriptionEntity subscription = subscriptionService
				.getSubscriptionByUuid(orderDto.getSubscriptionUuid());
		if (subscription == null) {
			throw new IllegalArgumentException("Subscription is invalid");
		}

		if (subscription.getSubscriptionType() == null) {
			throw new IllegalArgumentException("SubscriptionType is invalid");
		}

		final UserGridResponse<UserEntity> ptResp = userClient.getUser(orderDto.getPtUuid());
		if (ptResp.getEntities().isEmpty()) {
			throw new IllegalArgumentException("PT user is invalid");
		}
		final UserEntity ptUser = ptResp.getEntities().get(0);
		if (!ptUser.getLevel().equalsIgnoreCase(subscription.getSubscriptionType().getLevel().getCode())) {
			throw new IllegalArgumentException("Subscription and PT level are mismatch");
		}

		final String orderUuid = createOrderSubscription(subscription, ptUser, account.uuid());

		String url = StringUtils.EMPTY;
		if (orderDto.getInstrumentId() == null) {
			url = paymentService.processPaymentRequest(orderUuid, orderDto.getPaymentType(), account.language());
		} else {
			url = paymentService.handleInstrumentPayment(orderUuid, orderDto.getInstrumentId(), account);
		}

		return new CreateOrderResponse(orderUuid, url);
	}

	@Transactional
	public CreateOrderResponse processOrderCreditPackage(final CreateOrderCreditDTO orderDTO, final Account account)
			throws Exception {
		String orderUuid = StringUtils.EMPTY;
		;
		String url = StringUtils.EMPTY;

		try {
			final CreditPackageEntity creditEntity = creditPackageService
					.getCreditPackageById(orderDTO.getCreditPackageId());

			if (StringUtils.isEmpty(orderDTO.getPromotionCode())) {
				orderUuid = createOrderCreditPackage(creditEntity, account.uuid(), orderDTO.getReferalUserUuid(),
						orderDTO.getReferalUsername());
			} else {
				// Promotion code already validated at checking step
				Optional<PromotionEntity> promotion = promotionRepository.findByCouponCode(orderDTO.getPromotionCode());

				if (promotion.isPresent()) {
					orderUuid = createOrderCreditPackage(creditEntity, promotion.get(), account.uuid(),
							orderDTO.getReferalUserUuid(), orderDTO.getReferalUsername());
				} else {
					orderUuid = createOrderCreditPackage(creditEntity, account.uuid(), orderDTO.getReferalUserUuid(),
							orderDTO.getReferalUsername());
				}
			}

			if (orderDTO.getInstrumentId() == null) {
				url = paymentService.processPaymentRequest(orderUuid, orderDTO.getPaymentType(), account.language());
			} else {
				url = paymentService.handleInstrumentPayment(orderUuid, orderDTO.getInstrumentId(), account);
			}

		} catch (Exception e) {
			throw e;
		}

		return new CreateOrderResponse(orderUuid, url);
	}

	@Transactional
	public CreateOrderResponse processOrderCreditsAmount(OrderCreditsAmountRequest orderRequest, final Account account)
			throws Exception {
		String orderUuid = StringUtils.EMPTY;
		;
		String url = StringUtils.EMPTY;

		try {
			final Optional<CreditPackageEntity> optCreditPackage = creditPackageService.getUnitPackage();

			if (optCreditPackage.isPresent()) {
				CreditPackageEntity creditPackage = optCreditPackage.get();
				orderUuid = createOrderCreditsAmount(creditPackage, orderRequest.getCreditsAmount(), account.uuid(),
						orderRequest.getReferalUserUuid(), orderRequest.getReferalUsername());
			}

			if (orderRequest.getInstrumentId() == null) {
				url = paymentService.processPaymentRequest(orderUuid, orderRequest.getPaymentType(),
						account.language());
			} else {
				url = paymentService.handleInstrumentPayment(orderUuid, orderRequest.getInstrumentId(), account);
			}

		} catch (Exception e) {
			throw e;
		}

		return new CreateOrderResponse(orderUuid, url);
	}

	private String createOrderSubscription(SubscriptionEntity subscription, UserEntity ptUser, String userUuid) {

		final String orderUuid = UUID.randomUUID().toString();

		final OrderEntity order = new OrderEntity();
		order.setType(OrderType.SUBSCRIPTION.toString());
		order.setUserUuid(userUuid);
		order.setPtUuid(ptUser.getUuid());
		order.setUuid(orderUuid);
		order.setClubcode(ptUser.getClubcode());
		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		order.setCurrency(subscription.getCurrency());
		order.setCode(orderCodeGenerator.generate());
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setModifiedDate(now);
		order.setTotalPrice(subscription.getPrice());

		addOrderSubscriptionEntry(order, subscription);
		CommerceUtils.calculateOrder(order);
		orderService.createOrder(order);

		return orderUuid;
	}

	private String createOrderCreditsAmount(CreditPackageEntity creditPackage, final int creditsAmount,
			String ownerUuid, String referralUuid, String referralUsername) {

		final String orderUuid = UUID.randomUUID().toString();
		final int quantity = creditsAmount;
		final OrderEntity order = new OrderEntity();
		order.setType(OrderType.CREDIT.toString());
		order.setUuid(orderUuid);
		order.setUserUuid(ownerUuid);
		order.setPtUuid(StringUtils.EMPTY);
		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		order.setCurrency(creditPackage.getCurrency());
		order.setCode(orderCodeGenerator.generate());
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setModifiedDate(now);
		order.setTotalPrice(creditPackage.getPrice() * creditsAmount);
		order.setSubTotal(creditPackage.getPrice() * creditsAmount);

		addOrderCreditPackageEntry(order, creditPackage, quantity, ownerUuid, referralUuid, referralUsername);

		orderService.createOrder(order);

		return orderUuid;
	}

	private String createOrderCreditPackage(CreditPackageEntity creditPackage, String ownerUuid,
			final String referralUuid, final String referralUsername) {

		final String orderUuid = UUID.randomUUID().toString();
		final int quantity = 1;
		final OrderEntity order = new OrderEntity();
		order.setType(OrderType.CREDIT.toString());
		order.setUuid(orderUuid);
		order.setUserUuid(ownerUuid);
		order.setPtUuid(StringUtils.EMPTY);
		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		order.setCurrency(creditPackage.getCurrency());
		order.setCode(orderCodeGenerator.generate());
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setModifiedDate(now);
		order.setTotalPrice(creditPackage.getPrice());
		order.setSubTotal(creditPackage.getPrice());

		addOrderCreditPackageEntry(order, creditPackage, quantity, ownerUuid, referralUuid, referralUsername);

		orderService.createOrder(order);

		return orderUuid;
	}

	private String createOrderCreditPackage(CreditPackageEntity creditPackage, PromotionEntity promotion,
			String ownerUuid, final String referralUuid, final String referralUsername) {

		final String orderUuid = UUID.randomUUID().toString();
		final int quantity = 1;
		Double totalPrice = CommerceCouponUtils.calculatePromotionPrice(creditPackage.getPrice(),
				promotion.getDiscount());
		totalPrice = CommerceUtils.roundCurrency(totalPrice, creditPackage.getCurrency());
		Double subTotalPrice = creditPackage.getPrice();
		Double discountPrice = subTotalPrice - totalPrice;
		discountPrice = discountPrice < 0 ? 0 : discountPrice;

		final OrderEntity order = new OrderEntity();
		order.setType(OrderType.CREDIT.toString());
		order.setUuid(orderUuid);
		order.setUserUuid(ownerUuid);
		order.setPtUuid(StringUtils.EMPTY);
		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.NOTPAID);
		order.setCurrency(creditPackage.getCurrency());
		order.setCode(orderCodeGenerator.generate());
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setModifiedDate(now);
		order.setSubTotal(subTotalPrice);
		order.setTotalPrice(totalPrice);
		order.setDiscount(discountPrice);
		order.setCouponCode(promotion.getCouponCode());

		addOrderCreditPackageEntry(order, creditPackage, quantity, ownerUuid, referralUuid, referralUsername);

		orderService.createOrder(order);

		return orderUuid;
	}

	private void addOrderSubscriptionEntry(OrderEntity order, SubscriptionEntity subscription) {

		final OrderSubscriptionEntryEntity orderSubscriptionEntry = new OrderSubscriptionEntryEntity();
		orderSubscriptionEntry.setUnitPrice(subscription.getSubscriptionType().getUnitPrice());
		orderSubscriptionEntry.setUuid(UUID.randomUUID().toString());
		orderSubscriptionEntry.setQuantity(subscription.getNumberOfMonth());
		orderSubscriptionEntry.setPrice(subscription.getPrice());
		subscription.addOrderSubscriptionEntry(orderSubscriptionEntry);
		order.addOrderSubscriptionEntry(orderSubscriptionEntry);
	}

	private void addOrderCreditPackageEntry(OrderEntity order, CreditPackageEntity creditPackage, int quantity,
			String ownerUuid, final String referralUuid, final String referralUsername) {

		final OrderCreditEntryEntity orderCreditEntry = new OrderCreditEntryEntity();
		orderCreditEntry.setUnitPrice(creditPackage.getPrice());
		orderCreditEntry.setQuantity(quantity);
		orderCreditEntry.setOwnerUuid(ownerUuid);
		orderCreditEntry.setCreditPackage(creditPackage);
		orderCreditEntry.setReferrallUuid(referralUuid);
		orderCreditEntry.setReferralUsername(referralUsername);

		order.addOrderCreditEntry(orderCreditEntry);
	}

	/**
	 * Save Order into database.
	 *
	 * @param product
	 * @param productType
	 * @param ptUserEntity
	 * @param userUuid
	 * @param couponCode
	 */
	private String createOrderProduct(final ProductEntity product, final ProductTypeEntity productType,
			final UserEntity ptUserEntity, final String userUuid, final String couponCode, final Boolean freeOrder,
			String contractNumber) {
		final OrderEntity order = new OrderEntity();
		order.setUserUuid(userUuid);
		order.setPtUuid(ptUserEntity.getUuid());
		order.setClubcode(ptUserEntity.getClubcode());

		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.NOTPAID);

		order.setCurrency(product.getCurrency());

		final PromotionEntity promotion = (null == couponCode) ? null
				: promotionRepository.findOneByCouponCode(couponCode).get();
		if (null != promotion) {
			addProductToOrder(order, product, productType, promotion);
		} else {
			addProductToOrder(order, product, productType);
		}

		CommerceUtils.calculateOrder(order);

		final String orderUuid = UUID.randomUUID().toString();
		order.setCode(orderCodeGenerator.generate());
		order.setUuid(orderUuid);
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setType(OrderType.PRODUCT.toString());
		order.setModifiedDate(now);
		order.setFreeOrder(freeOrder);
		order.setContractNumber(contractNumber);
		orderService.createOrder(order);
		LOG.info("Finish create order {}", orderUuid);
		return orderUuid;
	}

	public String createCrmContract(final CrmContractDto contract, BasicUserEntity pt, BasicUserEntity eu) {

		String expireDate = contract.getExpireDate();
		if (StringUtils.isEmpty(expireDate)) {
			final LocalDate defaultExpiredDate = LocalDate.now().plusDays(contract.getNumOfSessions() * 4);
			expireDate = defaultExpiredDate.format(CommerceUtils.DATE_FORMATTER);
		}

		final FreeOrderRequest orderRequest = FreeOrderRequest.builder().contractNumber(contract.getContractNumber())
				.euUuid(eu.getUuid()).ptUuid(pt.getUuid()).expireDate(expireDate)
				.numOfSessions(contract.getNumOfSessions()).ptServiceFree(contract.getPtServiceFree()).build();

		return createFreeOrder(orderRequest, null);
	}

	/**
	 * First, we create new product that will not shown in product management. Then
	 * create free order with this new product.
	 *
	 * @param orderDto   request order free
	 * @param couponCode
	 */
	@Transactional
	public String createFreeOrder(final FreeOrderRequest orderDto, final String couponCode) {
		final UserGridResponse<UserEntity> euResp = userClient.getUser(orderDto.getEuUuid());
		if (euResp.isEmpty()) {
			throw new IllegalArgumentException("EU user is invalid");
		}
		final UserEntity euUser = euResp.getEntities().get(0);

		final UserGridResponse<UserEntity> ptResp = userClient.getUser(orderDto.getPtUuid());
		if (ptResp.isEmpty()) {
			throw new IllegalArgumentException("PT user is invalid");
		}
		final UserEntity ptUser = ptResp.getEntities().get(0);
		final String level = ptUser.getLevel();

		final String country = commerceProps.getCountry();
		final ProductTypeEntity productType = typeLevelService.getProductTypeByLevel(level.toUpperCase(Locale.US),
				country);
		// create new product
		LocalDate expireDate = null;
		try {
			expireDate = CommerceUtils.parseDate(orderDto.getExpireDate());
		} catch (Exception e) {
			expireDate = Instant.ofEpochMilli(Long.valueOf(orderDto.getExpireDate())).atZone(ZoneId.systemDefault())
					.toLocalDate();
		}
		if (expireDate.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Expire day must be in future");
		}
		final int expireLimit = (int) ChronoUnit.DAYS.between(LocalDate.now(), expireDate);

		final double promotionPrice = (orderDto.getPtServiceFree() * 100) / (100 - productType.getCommision());

		final Product productDto = Product.builder().active(Boolean.TRUE).visibility(Boolean.FALSE).levelCode(level)
				.expireLimit(expireLimit).numOfSessions(orderDto.getNumOfSessions())
				.commision(productType.getCommision()).promotionPrice(promotionPrice)
				.trainingStyle(orderDto.getTrainingStyle()).build();

		final ProductEntity product = productService.createProduct(productDto, country, productType.getCurrency());

		final String orderUuid = createOrderProduct(product, productType, ptUser, euUser.getUuid(), couponCode, true,
				orderDto.getContractNumber());

		// publish event when order create success
		commerceEventService.publishOrderCompleteEvent(orderUuid);

		return orderUuid;
	}

	/**
	 * First, we create new product that will not shown in product management. Then
	 * create free orders with this new product.
	 *
	 * @param orderDto   request order free
	 * @param couponCode
	 */
	@Transactional
	public List<String> createFreeOrders(final List<FreeOrderRequest> orderDtos) {
		final List<String> result = new ArrayList<String>();
		final List<String> contract_numbers = new ArrayList<String>();
		for (final FreeOrderRequest request : orderDtos) {
			result.add(createFreeOrder(request, null));
			contract_numbers.add(request.getContractNumber());
		}
		orderService.updateContractUploadStatus(contract_numbers);
		return result;
	}

	public Order getOrderByUuid(final String orderUuid, final Account account, final String language) {
		final OrderEntity order = orderService.getOrderByUser(orderUuid, account.uuid());
		if (order == null) {
			throw new IllegalArgumentException(
					String.format("Order not found uuid %s, user %s", orderUuid, account.uuid()));
		}

		final List<OrderEntry> products = order.getOrderProductEntries().stream()
				.map(entry -> buildOrderEntry(entry, order.getCurrency(), language)).collect(Collectors.toList());

		final Date createDate = Date.from(order.getCreatedDate().toInstant(ZoneOffset.UTC));

		return Order.builder().code(order.getCode()).createdDate(createDate).currency(order.getCurrency())
				.id(order.getUuid()).orderStatus(order.getOrderStatus()).paymentStatus(order.getPaymentStatus())
				.subTotal(order.getSubTotal()).totalPrice(order.getTotalPrice()).ptUuid(order.getPtUuid())
				.userUuid(order.getUserUuid()).addAllProducts(products).build();
	}

	public Order getOrderByUuid(final String orderUuid, final String language) {
		final OrderEntity order = orderService.getOrder(orderUuid);
		if (order == null) {
			throw new IllegalArgumentException(String.format("Order not found uuid %s", orderUuid));
		}

		final List<OrderEntry> products = order.getOrderProductEntries().stream()
				.map(entry -> buildOrderEntry(entry, order.getCurrency(), language)).collect(Collectors.toList());

		final Date createDate = Date.from(order.getCreatedDate().toInstant(ZoneOffset.UTC));

		return Order.builder().code(order.getCode()).createdDate(createDate).currency(order.getCurrency())
				.id(order.getUuid()).orderStatus(order.getOrderStatus()).paymentStatus(order.getPaymentStatus())
				.subTotal(order.getSubTotal()).totalPrice(order.getTotalPrice()).ptUuid(order.getPtUuid())
				.userUuid(order.getUserUuid()).addAllProducts(products).contractNumber(order.getContractNumber())
				.build();
	}

	public List<OrderHistory> getOrderByUser(final String userUuid, final String language) {
		final List<Object[]> orders = orderService.getOrderHistoryByUser(userUuid);
		return orders.stream().map(order -> convertToOrderHistory(order, language)).collect(Collectors.toList());
	}

	private void addProductToOrder(final OrderEntity order, final ProductEntity product,
			final ProductTypeEntity productType, final PromotionEntity promotion) {
		Double discount = (null != promotion) ? promotion.getDiscount() : 0;
		// TODO this is hard code for August 2019 campaign, should be removed later and
		// return discount
		//
		if (promotion != null) {
			discount = CommerceCouponUtils.calculatorSpecialDiscount(discount, promotion.getCouponCode(),
					product.getNumOfSessions());
		}
		// end

		final Integer freeSession = (null != promotion) ? promotion.getFreeSession() : 0;
		order.setCouponCode(promotion.getCouponCode());
		final OrderEntryEntity orderEntry = new OrderEntryEntity();
		orderEntry.setUnitPrice(productType.getUnitPrice());
		orderEntry.setQuantity(product.getNumOfSessions() + freeSession);
		orderEntry.setExpireLimit(product.getExpireLimit());
		orderEntry.setEntryNumber(1);
		orderEntry.setCommision(productType.getCommision());

		orderEntry.setSubTotal(CommerceUtils.calculateSubTotal(productType.getUnitPrice(), product.getNumOfSessions()));
		final Double totalPrice = CommerceUtils.roundCurrency(
				CommerceCouponUtils.calculateFinalTotalPrice(product, productType.getUnitPrice(), discount),
				order.getCurrency());
		orderEntry.setTotalPrice(totalPrice);
		orderEntry.setProduct(product);

		order.addOrderProductEntry(orderEntry);
	}

	private void addProductToOrder(final OrderEntity order, final ProductEntity product,
			final ProductTypeEntity productType) {
		final OrderEntryEntity orderEntry = new OrderEntryEntity();
		orderEntry.setUnitPrice(productType.getUnitPrice());
		orderEntry.setQuantity(product.getNumOfSessions());
		orderEntry.setExpireLimit(product.getExpireLimit());
		orderEntry.setEntryNumber(1);
		orderEntry.setCommision(productType.getCommision());

		orderEntry.setSubTotal(CommerceUtils.calculateSubTotal(productType.getUnitPrice(), product.getNumOfSessions()));
		orderEntry.setTotalPrice(CommerceUtils.roundCurrency(product.getPromotionPrice(), order.getCurrency()));
		orderEntry.setProduct(product);

		order.addOrderProductEntry(orderEntry);
	}

	private OrderEntry buildOrderEntry(final OrderEntryEntity entity, final String currency, final String language) {
		final String displayUnitPrice = CurrencyUtils.formatCurrency(currency, language, entity.getUnitPrice());
		final String displaySubTotal = CurrencyUtils.formatCurrency(currency, language, entity.getSubTotal());
		final String displayTotalPrice = CurrencyUtils.formatCurrency(currency, language, entity.getTotalPrice());
		return OrderEntry.builder().numOfSessions(entity.getQuantity()).unitPrice(entity.getUnitPrice())
				.displayUnitPrice(displayUnitPrice).entryNumber(entity.getEntryNumber())
				.expireLimit(entity.getExpireLimit()).subTotal(entity.getSubTotal()).displaySubTotal(displaySubTotal)
				.totalPrice(entity.getTotalPrice()).displayTotalPrice(displayTotalPrice).build();
	}

	private OrderHistory convertToOrderHistory(final Object[] row, final String language) {
		Integer expireDays = (Integer) row[7];

		if (null != row[3]) {
			final int days = Long.valueOf(
					TimeUnit.DAYS.convert(((Timestamp) row[3]).getTime() - new Date().getTime(), TimeUnit.MILLISECONDS))
					.intValue();
			expireDays = days >= 0 ? (expireDays - days) : null;
		}
		final String displayTotalPrice = CurrencyUtils.formatCurrency((String) row[5], language, (Double) row[4]);
		final Date createDate = new Date(((Timestamp) row[2]).getTime());
		return OrderHistory.builder().id((String) row[0]).code((String) row[1]).createdDate(createDate)
				.displayTotalPrice(displayTotalPrice).numberOfSession((Integer) row[6]).expireIn(expireDays)
				.trainerName((String) row[8]).build();
	}

	public CreateOrderCodResponse processOrderCreditsCoupons(String code, Account account) throws Exception {
		try {
			String decryptedCode = AESEncryptUtils.decrypt(code, commerceProps.getSecretKey());
			Optional<CreditCouponEntity> creditCoupon = creditCouponsRepository.findByCode(decryptedCode);
			if (!creditCoupon.isPresent()) {
				throw new CreditCouponException(CommerceErrorConstantCode.COUPON_INVALID);
			}
			CreditCouponEntity creditCouponEntity = creditCoupon.get();
			if (!creditCouponEntity.getStatus().equals(CreditCouponStatus.ACTIVED)) {
				throw new CreditCouponException(CommerceErrorConstantCode.COUPON_INACTIVE);
			}
			LocalDateTime expiredDate = creditCouponEntity.getCouponExpiredDate();
			if (expiredDate != null && expiredDate.isBefore(LocalDateTime.now())) {
				creditCouponEntity.setStatus(CreditCouponStatus.EXPIRED);
				creditCouponsRepository.save(creditCouponEntity);
				throw new CreditCouponException(CommerceErrorConstantCode.COUPON_EXPIRED);
			}
			OrderEntity order = createOrderCreditCoupon(creditCouponEntity, account.uuid());
			topUpUserWallet(creditCouponEntity, account.uuid());
			createCreditCouponOwnerEntity(creditCouponEntity, order);
			order.setOrderStatus(OrderStatus.COMPLETED);
			orderService.save(order);
			creditCouponEntity.setStatus(CreditCouponStatus.USED);
			creditCouponsRepository.save(creditCouponEntity);
			return buildResponse(creditCouponEntity, order);
		} catch (Exception e) {
			throw e;
		}
	}

	private OrderEntity createOrderCreditCoupon(CreditCouponEntity creditCoupon, String ownerUuid) {

		final String orderUuid = UUID.randomUUID().toString();
		final OrderEntity order = new OrderEntity();
		order.setType(OrderType.COUPON.toString());
		order.setUuid(orderUuid);
		order.setUserUuid(ownerUuid);
		order.setPtUuid(StringUtils.EMPTY);
		order.setOrderStatus(OrderStatus.CREATED);
		order.setPaymentStatus(PaymentStatus.PAID);
		order.setCode(orderCodeGenerator.generate());
		order.setCurrency("VND");
		final LocalDateTime now = LocalDateTime.now();
		order.setCreatedDate(now);
		order.setModifiedDate(now);
		order.setTotalPrice(creditCoupon.getAmount());
		order.setSubTotal(creditCoupon.getAmount());

		addOrderCreditCouponEntry(order, creditCoupon, ownerUuid);

		orderService.createOrder(order);

		return order;
	}

	private void addOrderCreditCouponEntry(OrderEntity order, CreditCouponEntity creditCoupon, String ownerUuid) {

		final OrderCouponEntryEntity orderCouponEntry = new OrderCouponEntryEntity();
		orderCouponEntry.setCreditCoupon(creditCoupon);
		orderCouponEntry.setOrder(order);
		orderCouponEntry.setOwnerUuid(ownerUuid);
		orderCouponEntry.setUnitPrice(creditCoupon.getAmount());

		order.addOrderCouponEntryEntity(orderCouponEntry);
	}

	private void createCreditCouponOwnerEntity(CreditCouponEntity creditCoupon, OrderEntity orderEntity) {
		try {
			int numberOfExpiredDay = creditCoupon.getCreditExpiredDay();
			LocalDateTime expiredDate = LocalDateTime.now();
			expiredDate = expiredDate.plusDays(numberOfExpiredDay + 1).withHour(0).withMinute(0).withSecond(0)
					.withNano(0);

			UserCreditPackageEntity entity = new UserCreditPackageEntity();
			entity.setOrder(orderEntity);
			entity.setExpiredDate(expiredDate);
			entity.setOwnerUuid(orderEntity.getUserUuid());
			entity.setTotalCredit(creditCoupon.getCredit() + creditCoupon.getBonusCredit());
			creditPackageOwnerRepo.save(entity);
		} catch (Exception e) {
			LOG.error("Could not create credit package owner with exception: {}", e.getMessage());
		}
	}

	@Transactional(rollbackFor = Exception.class)
	private void topUpUserWallet(CreditCouponEntity creditCouponEntity, String ownerUuid) throws Exception {
		try {
			int credit = creditCouponEntity.getCredit() + creditCouponEntity.getBonusCredit();
			double amount = creditCouponEntity.getAmount();
			CreditTransactionType transactionType = CreditTransactionType.TOPUP;
			CreditTransactionStatus transactionStatus = CreditTransactionStatus.COMPLETED;
			String description = CreditTransactionType.TOPUP.getText() + "_" + creditCouponEntity.getId() + "_"
					+ credit;
			BasicUserEntity userInfo = userService.getOneByUuid(ownerUuid);
			CreditWalletEntity creditWallet = walletService.getWalletByOwnerUuid(ownerUuid);
			if (creditWallet == null) {
				Boolean active = Boolean.TRUE;
				creditWallet = new CreditWalletEntity(ownerUuid, active);
				walletService.createWallet(creditWallet);
			}
			int currentCreditBalance = creditWallet.getTotalCredit();
			CreditWalletLevel currentWalletLevel = creditWallet.getLevel();

			// Topup credit to user wallet
			creditWallet = walletService.topupCreditWallet(ownerUuid, credit, amount);
			CreditWalletLevel newWalletLevel = creditWallet.getLevel();

			// Recording the TOPUP credit wallet transaction
			List<String> descriptionParams = Arrays.asList(String.valueOf(creditCouponEntity.getName()));
			walletTransactionService.createCreditWalletTransaction(creditWallet.getId(), ownerUuid,
					userInfo.getAvatar(), currentCreditBalance, credit, transactionType, transactionStatus, description,
					descriptionParams);

			// Recording the UPGRADE WALLET LEVEL if any
			if (newWalletLevel != currentWalletLevel) {
				descriptionParams = Arrays.asList(String.valueOf(newWalletLevel.name()));
				transactionType = CreditTransactionType.WALLET_LEVEL_UPGRADE;
				walletTransactionService.createCreditWalletTransaction(creditWallet.getId(), ownerUuid,
						userInfo.getAvatar(), currentCreditBalance, credit, transactionType, transactionStatus,
						description, descriptionParams);
			}
		} catch (Exception e) {
			LOG.error("Top up wallet failed with exception: {}", e.getMessage());
			throw e;
		}
	}

	private CreateOrderCodResponse buildResponse(CreditCouponEntity entity, OrderEntity order) {
		CreateOrderCodResponse response = new CreateOrderCodResponse();
		response.setOrderId(order.getId());
		response.setCouponCredit(entity.getCredit());
		response.setBonusCredit(entity.getBonusCredit());
		response.setCouponAmount(entity.getAmount());
		response.setStatus(entity.getStatus());
		response.setSerial(entity.getSerial());
		response.setCouponName(entity.getName());
		BasicUserEntity userInfo = userService.getOneByUuid(order.getUserUuid());
		BasicUserInfo owner = new BasicUserInfo();
		owner.setUuid(order.getUserUuid());
		owner.setAvatar(userInfo.getAvatar());
		owner.setFullName(userInfo.getFullName());
		owner.setUsername(userInfo.getUserName());
		owner.setPhone(userInfo.getPhone());
		owner.setEmail(userInfo.getEmail());
		response.setOwner(owner);
		response.setCreditExpired(LocalDate.now().plusDays(entity.getCreditExpiredDay() + 1)
				.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
		return response;
	}
}