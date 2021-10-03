package asia.cmg.f8.commerce.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import asia.cmg.f8.commerce.client.FollowUserClient;
import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.config.PaymentProperties;
import asia.cmg.f8.commerce.config.PaymentProperties.PaymentModule;
import asia.cmg.f8.commerce.constants.PaymentConstant;
import asia.cmg.f8.commerce.constants.PaymentType;
import asia.cmg.f8.commerce.constants.TransactionCommand;
import asia.cmg.f8.commerce.constants.TransactionResponseCode;
import asia.cmg.f8.commerce.constants.TransactionStatus;
import asia.cmg.f8.commerce.dto.InstrumentInfoDto;
import asia.cmg.f8.commerce.dto.PaymentTransactionEntryType;
import asia.cmg.f8.commerce.dto.PaymentTransactionType;
import asia.cmg.f8.commerce.dto.TransactionStatusInfo;
import asia.cmg.f8.commerce.dto.onepay.InstrumentDto;
import asia.cmg.f8.commerce.dto.onepay.IssuerDto;
import asia.cmg.f8.commerce.dto.onepay.LinkDto;
import asia.cmg.f8.commerce.dto.onepay.TokenDto;
import asia.cmg.f8.commerce.dto.onepay.TransactionDto;
import asia.cmg.f8.commerce.dto.onepay.UserDto;
import asia.cmg.f8.commerce.dto.onepay.PaymentResponseDto;
import asia.cmg.f8.commerce.dto.onepay.PspType;
import asia.cmg.f8.commerce.entity.BasicUserEntity;
import asia.cmg.f8.commerce.entity.OnepayInstrumentEntity;
import asia.cmg.f8.commerce.entity.OnepayUserEntity;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderType;
import asia.cmg.f8.commerce.entity.PaymentTransactionEntity;
import asia.cmg.f8.commerce.entity.PaymentTransactionEntryEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditPackageType;
import asia.cmg.f8.commerce.entity.credit.UserCreditPackageEntity;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionStatus;
import asia.cmg.f8.commerce.entity.credit.CreditTransactionType;
import asia.cmg.f8.commerce.entity.credit.CreditWalletEntity;
import asia.cmg.f8.commerce.entity.credit.CreditWalletLevel;
import asia.cmg.f8.commerce.entity.credit.CreditWalletTransactionEntity;
import asia.cmg.f8.commerce.entity.credit.OrderCreditEntryEntity;
import asia.cmg.f8.commerce.exception.RequestPaymentException;
import asia.cmg.f8.commerce.repository.UserCreditPackageRepository;
import asia.cmg.f8.commerce.repository.CreditWalletRepository;
import asia.cmg.f8.commerce.repository.CreditWalletTransactionRepository;
import asia.cmg.f8.commerce.repository.OnepayUserRepository;
import asia.cmg.f8.commerce.utils.CommerceUtils;
import asia.cmg.f8.commerce.utils.OnepayUtils;
import asia.cmg.f8.commerce.utils.PaymentUtils;
import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;

@Service
@Transactional(readOnly = true)
public class PaymentService {

	private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

	private final OrderService orderService;

	private final CommerceEventService commerceEventService;

	private final CommerceProperties commerceProps;

	private final PaymentProperties paymentProps;

	private final FollowUserClient followUserClient;

	private final UserService userService;

	private final OnepayUserRepository onepayUserRepository;

	private final OnepayInstrumentService onepayInstrumentService;

	private final ObjectMapper objectMapper;
	
	@Autowired
	private CreditWalletTransactionService walletTransactionService;
	
	@Autowired
	private UserCreditPackageRepository creditPackageOwnerRepo;
	
	@Autowired
	private WalletService walletService;

	@Inject
	public PaymentService(final OrderService orderService, final CommerceEventService commerceEventService,
			final CommerceProperties commerceProps, final PaymentProperties paymentProps,
			final FollowUserClient followUserClient, final UserService userService,
			final OnepayUserRepository onepayUserRepository, OnepayInstrumentService onepayInstrumentService,
			ObjectMapper objectMapper) {

		this.orderService = orderService;
		this.commerceEventService = commerceEventService;
		this.commerceProps = commerceProps;
		this.paymentProps = paymentProps;
		this.followUserClient = followUserClient;
		this.userService = userService;
		this.onepayUserRepository = onepayUserRepository;
		this.onepayInstrumentService = onepayInstrumentService;
		this.objectMapper = objectMapper;
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Start process payment.
	 * 
	 * @param orderUuid   order uuid
	 * @param paymentType domestic or international payment
	 * @param language    display language on payment gateway.
	 * @return request url
	 */
	public String processPaymentRequest(final String orderUuid, final PaymentType paymentType, final String language) {
		LOG.info("Start process payment for order {}", orderUuid);
		final OrderEntity order = orderService.getOrder(orderUuid);
		if (order == null) {
			throw new IllegalArgumentException(String.format("Order not found uuid %s", orderUuid));
		}
//        if (paymentType == null) {
//            paymentType = PaymentType.DOMESTIC;
//        }

		try {
			return buildRequestPayment(order, (paymentType != null ? paymentType : PaymentType.DOMESTIC), language);
		} catch (final UnsupportedEncodingException excpt) {
			LOG.error("Error when trying to build request url {}", excpt);
			throw (RequestPaymentException) new RequestPaymentException("Error building request url").initCause(excpt);
		}
	}

	/**
	 * Process response data from payment gateway.
	 * 
	 * @param fields response fields
	 * @return process result
	 */
	@Transactional
	public TransactionStatusInfo processResponseData(final Map<String, Object> fields, final boolean domestic) {
		final Map<String, Object> processFields = new HashMap<>(fields);
		LOG.info("Transaction response info: {}", processFields.toString());
		
		String hashValidated;
		final String transactionSecureHash = PaymentUtils
				.nullToUnknown((String) processFields.remove(PaymentConstant.VCP_SECURE_HASH));

		final String txnResponseCode = PaymentUtils
				.nullToUnknown((String) processFields.get(PaymentConstant.VCP_TRANSACTION_RESPONSE_CODE));
		LOG.info("Transaction response code is {}", txnResponseCode);

		if (hasReturnData(txnResponseCode)) {
			final String secretKey = domestic ? paymentProps.getDomestic().getSecretKey()
					: paymentProps.getInternational().getSecretKey();
			final String secureHash = PaymentUtils.hashAllFields(processFields, secretKey);
			if (transactionSecureHash.equalsIgnoreCase(secureHash)) {
				hashValidated = PaymentConstant.HASH_VALID;
			} else {
				hashValidated = PaymentConstant.HASH_INVALID;
			}
		} else {
			hashValidated = PaymentConstant.HASH_INVALID;
		}

		LOG.info("Hash validation result: {}", hashValidated);

		if (hashValidated.equals(PaymentConstant.HASH_INVALID)) {
			LOG.info("Transaction data has been modified, ignored this request");
			return new TransactionStatusInfo(TransactionStatus.INVALID_HASH, "Transaction data has been modified");
		}
		final String orderCode = PaymentUtils.nullToUnknown((String) processFields.get(PaymentConstant.VCP_ORDER_INFO));

		final OrderEntity order = orderService.getOrderByCode(orderCode);
		if (order == null) {
			LOG.error("Order not found: {}", orderCode);
			return new TransactionStatusInfo(TransactionStatus.IGNORE, "Order not found");
		}

		final TransactionStatusInfo transactionStatusInfo = validateAndStoreResponsePaymentData(order, fields);
		transactionStatusInfo.setUserUuid(order.getUserUuid());

		// Create the following connection between EU & PT
		if(OrderType.PRODUCT.name().compareToIgnoreCase(order.getType()) == 0
				|| OrderType.SUBSCRIPTION.name().compareToIgnoreCase(order.getType()) == 0) {
			followUserClient.createFollowingConnection(order.getUserUuid(), order.getPtUuid());
			followUserClient.createFollowingConnection(order.getPtUuid(), order.getUserUuid());
		}

		LOG.info("Finish process payment response data with status {} : {}",
				transactionStatusInfo.getStatus().getCode(), transactionStatusInfo.getDetail());

		if (TransactionStatus.SUCCESS.equals(transactionStatusInfo.getStatus())
				&& PaymentUtils.isPaymentSuccess(order)) {
			
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

					// Fired success order event
					commerceEventService.publishPurchasedPackageEvent(orderCreditPackage);

					// Recording the UPGRADE WALLET LEVEL if any
					if(newWalletLevel != currentWalletLevel) {
						descriptionParams = Arrays.asList(String.valueOf(newWalletLevel.name()));
						transactionType = CreditTransactionType.WALLET_LEVEL_UPGRADE;
						walletTransactionService.createCreditWalletTransaction(creditWallet.getId(), ownerUuid, userInfo.getAvatar(), 
																		currentCreditBalance, creditAmount, transactionType, 
																		transactionStatus, description, descriptionParams);
						// Fired upgraded wallet event
						commerceEventService.publishUpgradedWalletEvent(currentWalletLevel, creditWallet);
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
			
			// store card info for next payment
			this.storeInstrumentPayment(order, processFields, domestic);
			
			// Fired order completed event
			commerceEventService.publishOrderCompleteEvent(order.getUuid());
		}

		return transactionStatusInfo;
	}

	private void storeInstrumentPayment(OrderEntity order, Map<String, Object> processFields, boolean domestic) {
		try {
			final PaymentTransactionEntity transaction = getDefaultTransaction(order);
			if (transaction.getInstrumentId() == null) {
				storeOnepayInfo(order, processFields, domestic ? PspType.D : PspType.I);
			}
		} catch (Exception e) {
			LOG.error("[storeInstrumentPayment] error detail: {}", e.getMessage());
		}
	}
	
	public String handleInstrumentPayment(final String orderUuid, final Long instrumentId, Account account) {

		LOG.info("Start handle instrument payment for order {}", orderUuid);

		final OrderEntity order = orderService.getOrder(orderUuid);
		if (order == null) {
			LOG.error("Order not found uuid {}", orderUuid);
			throw new IllegalArgumentException(String.format("Order not found uuid %s", orderUuid));
		}

		if (!StringUtils.equals(order.getUserUuid(), account.uuid())) {
			LOG.error("User {} can not payment order {}", account.uuid(), orderUuid);
			throw new RequestPaymentException(
					String.format("User %s can not payment order %s", account.uuid(), orderUuid));
		}

		final OnepayInstrumentEntity instrument = onepayInstrumentService.findOneById(instrumentId).get();
		final OnepayUserEntity onepayUser = onepayUserRepository.findOneById(instrument.getOnepayUserId()).get();
		if (!StringUtils.equals(onepayUser.getUserUuid(), account.uuid())) {
			LOG.error("User {} is not mapping with onepayuser {}", account.uuid(), onepayUser.getUserUuid());
			throw new RequestPaymentException(String.format("User %s is not mapping with onepayuser %s", account.uuid(),
					onepayUser.getUserUuid()));
		}

		final PaymentModule paymentModule = PspType.I.equals(instrument.getPspId()) ? paymentProps.getInternational()
				: paymentProps.getDomestic();

		final Double amount = PaymentUtils.getAmount(paymentProps, paymentModule, order.getTotalPrice());

		final String transactionRef = CommerceUtils.getUniqueCode();

		try {

			final String requestData = objectMapper.writeValueAsString(OnepayUtils.buildPaymentRequest(instrument,
					onepayUser, order, transactionRef, amount, paymentModule.getReturnUrl()));

			final ResponseEntity<String> result = OnepayUtils.sendRequest(HttpMethod.POST, OnepayUtils.TSP_PAYMENT_URL,
					requestData, paymentProps);
			LOG.info("result body payment status: {} with request {}", result.getStatusCode(), requestData);

			final PaymentResponseDto paymentResponse = objectMapper.readValue(result.getBody(),
					PaymentResponseDto.class);

			if (HttpStatus.CREATED.equals(result.getStatusCode())
					&& !PaymentConstant.STATE_APPROVED.equals(paymentResponse.getState())) {

				storeTransaction(order, instrument, transactionRef, requestData, amount);

				final LinkDto link = paymentResponse.getLinks();
				if (PspType.I.equals(instrument.getPspId()) && link != null && link.getInfoUpdate() != null) {
					return link.getInfoUpdate().getHref();
				} else if (paymentResponse.getAuthorization() != null) {
					return paymentResponse.getAuthorization().getLinks().getApproval().getHref();
				}
			}
			LOG.error("Can not creat payment link for order {}", orderUuid);
			throw new RequestPaymentException(String.format("Can not creat payment link for order %s", orderUuid));

		} catch (final Exception excpt) {
			LOG.error("Error when trying to build request url {}", excpt);
			throw new RequestPaymentException("Error send payment reques");
		}
	}

	private void storeTransaction(final OrderEntity order, final OnepayInstrumentEntity instrument,
			final String transactionRef, final String requestData, final Double amount) {
		final PaymentTransactionEntity transaction = new PaymentTransactionEntity();
		final LocalDateTime now = LocalDateTime.now();
		transaction.setPaymentProvider(PaymentConstant.PAYMENT_PROVIDER);
		transaction.setCurrency(order.getCurrency());
		transaction.setAmount(amount);
		transaction.setCreatedDate(now);
		transaction.setRequestId(transactionRef);
		transaction.setTransactionType(PaymentTransactionType.PAY);
		transaction.setPaymentType(
				PspType.I.equals(instrument.getPspId()) ? PaymentType.INTERNATIONAL : PaymentType.DOMESTIC);
		transaction.setInstrumentId(instrument.getId());

		final PaymentTransactionEntryEntity transactionEntry = new PaymentTransactionEntryEntity();
		transactionEntry.setAmount(amount);
		transactionEntry.setCurrency(order.getCurrency());
		transactionEntry.setRequestId(transactionRef);

		transactionEntry.setCreatedDate(now);
		transactionEntry.setTransactionDate(now);
		transactionEntry.setTransactionType(PaymentTransactionEntryType.REQUEST);
		transactionEntry.setRequestData(requestData);
		transactionEntry.setVersionId(paymentProps.getVersionModule());

		transaction.addTransactionEntry(transactionEntry);
		order.addPaymentTransaction(transaction);
		order.setPaymentStatus(PaymentStatus.WAITING_PAYMENT);

		orderService.save(order);
	}

	private void storeOnepayInfo(final OrderEntity order, final Map<String, Object> paymentResFields,
			final PspType pspId) {
		final BasicUserEntity userEntity = userService.getOneByUuid(order.getUserUuid());
		final Optional<OnepayUserEntity> existingOnepayUser = onepayUserRepository
				.findOneByUserUuid(userEntity.getUuid());
		final OnepayUserEntity onepayUser = existingOnepayUser.isPresent() ? existingOnepayUser.get()
				: createOnepayUser(userEntity, paymentResFields);
		if (onepayUser != null) {
			final TransactionDto transactionRequest = new TransactionDto(pspId,
					OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VCP_MERCHANT),
					OnepayUtils.getValFromMap(paymentResFields, PaymentConstant.VCP_MERCHANT_TRANSACTION_REF));

			createOnepayInstrument(onepayUser, pspId, transactionRequest);
		}
	}

	private OnepayUserEntity createOnepayUser(final BasicUserEntity user, final Map<String, Object> paymentResFields) {
		try {
			final String request = objectMapper
					.writeValueAsString(OnepayUtils.buildUserRequest(paymentProps, user, paymentResFields));
			final ResponseEntity<String> result = OnepayUtils.sendRequest(HttpMethod.POST, OnepayUtils.TSP_USERS_URL,
					request, paymentProps);
			LOG.info("result body user status {} with request {}", result.getStatusCode(), request);

			if (HttpStatus.CREATED.equals(result.getStatusCode())) {
				final UserDto userResponse = objectMapper.readValue(result.getBody(), UserDto.class);

				final OnepayUserEntity onepayUserEntity = new OnepayUserEntity();
				onepayUserEntity.setGroupId(userResponse.getGroupId());
				onepayUserEntity.setOnepayUserId(userResponse.getId());
				onepayUserEntity.setRefId(userResponse.getRefId());
				onepayUserEntity.setState(userResponse.getState());
				onepayUserEntity.setUserUuid(user.getUuid());
				onepayUserEntity.setCreatedDate(LocalDateTime.now());
				onepayUserEntity.setModifiedDate(LocalDateTime.now());
				onepayUserEntity.setOnepayUserId(userResponse.getId());
				return onepayUserRepository.save(onepayUserEntity);
			}

		} catch (final Exception e) {
			LOG.error("Can not create onepay user: {}", e);
		}
		LOG.error("Can not create onepay user for : {}", user.getUuid());
		return null;
	}
	
//	private void createCreditWalletTransaction(final int creditAmount, final long creditWalletId,final String ownerUuid, 
//												final CreditTransactionType transactionType, final CreditTransactionStatus transactionStatus, final String description) {
//		try {
//			CreditWalletTransactionEntity walletTransaction = new CreditWalletTransactionEntity();
//			
//			walletTransaction.setCreditAmount(creditAmount);
//			walletTransaction.setCreditWalletId(creditWalletId);
//			walletTransaction.setDescription(description);
//			walletTransaction.setOwnerUuid(ownerUuid);
//			walletTransaction.setTransactionStatus(transactionStatus.getValue());
//			walletTransaction.setTransactionType(transactionType.getValue());
//			
//			walletTransactionRepo.save(walletTransaction);
//		} catch (Exception e) {
//			LOG.error("Could not create credit wallet transaction with exception: {}", e.getMessage());
//		}
//	}
	
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

	private void createOnepayInstrument(final OnepayUserEntity onepayUser, final PspType pspId,
			final TransactionDto transactionReq) {
		try {

			final String request = objectMapper
					.writeValueAsString(OnepayUtils.buildInstrumentRequest(onepayUser, transactionReq));
			final ResponseEntity<String> result = OnepayUtils.sendRequest(HttpMethod.POST,
					OnepayUtils.TSP_INSTRUMENT_URL, request, paymentProps);
			LOG.info("result body instrument status {} with request {}", result.getStatusCode(), request);

			if (HttpStatus.CREATED.equals(result.getStatusCode())) {
				final InstrumentDto instrumentDto = objectMapper.readValue(result.getBody(), InstrumentDto.class);

				final String instrumentId = instrumentDto.getInstrumentId();
				if (PaymentConstant.STATE_APPROVED.equals(instrumentDto.getState())) {
					final OnepayInstrumentEntity instrument = new OnepayInstrumentEntity();
					final LocalDateTime now = LocalDateTime.now();
					instrument.setInstrumentId(instrumentId);
					instrument.setCreatedDate(now);
					instrument.setModifiedDate(now);
					instrument.setMerchantId(transactionReq.getMerchantId());
					instrument.setMerchantTxnRef(transactionReq.getMerchantTxtRef());
					instrument.setName(instrumentDto.getName());
					instrument.setNumber(instrumentDto.getNumber());
					instrument.setNumberHash(instrumentDto.getNumberHash());
					instrument.setOnepayUserId(onepayUser.getId());
					instrument.setPspId(pspId);

					final IssuerDto issuer = instrumentDto.getIssuer();
					if (issuer != null && issuer.getBrand() != null) {
						instrument.setBrandId(issuer.getBrand().getId());
						instrument.setBrandName(issuer.getBrand().getName());
					}

					final TokenDto token = instrumentDto.getToken();
					if (token != null) {
						instrument.setTokenCvv(token.getIcvv());
						instrument.setTokenExpireMonth(token.getExpireMonth());
						instrument.setTokenExpireYear(token.getExpireYear());
						instrument.setTokenId(token.getId());
						instrument.setTokenNumber(token.getNumber());
					}

					instrument.setType(instrumentDto.getType());
					onepayInstrumentService.save(instrument);
				} else {
					LOG.error("Can not create instrument for user {}", onepayUser.getUserUuid());
				}

			}
		} catch (final Exception e) {
			LOG.error("Can not create instrument:", e);
		}
	}

	public TransactionStatus removeOnepayInstrument(final Long instrumentId, final Account account) {
		try {
			final Optional<OnepayInstrumentEntity> instrumentEntity = onepayInstrumentService.findOneById(instrumentId);
			if (!instrumentEntity.isPresent()) {
				LOG.error("Not found instrument {}", instrumentId);
				throw new IllegalArgumentException("Not found instrument");
			}
			final List<InstrumentInfoDto> instruments = onepayInstrumentService.findAllInstruments(account.uuid());
			if (!instruments.stream().anyMatch(x -> x.getId() == instrumentId)) {
				throw new IllegalArgumentException("Do not have permission to delete instrument");
			}
			final ResponseEntity<String> result = OnepayUtils.sendRequest(HttpMethod.DELETE,
					OnepayUtils.TSP_INSTRUMENT_URL + "/" + instrumentEntity.get().getInstrumentId(), null,
					paymentProps);
			LOG.info("Remove instrument response status {}: {}", result.getStatusCode(), result.getBody());
			if (HttpStatus.OK.equals(result.getStatusCode())) {
				onepayInstrumentService.deleteInstrument(instrumentEntity.get());
				return TransactionStatus.SUCCESS;
			}

		} catch (final Exception e) {
			LOG.error("Can not delete instrument:", e);

		}
		return TransactionStatus.FAIL;
	}

	/**
	 * Build payment request url to onepay.
	 * 
	 * @param order       order
	 * @param paymentType payment type
	 * @return request url
	 * @throws UnsupportedEncodingException exception
	 */
	private String buildRequestPayment(final OrderEntity order, final PaymentType paymentType, final String language)
			throws UnsupportedEncodingException {
		final String transactionRef = CommerceUtils.getUniqueCode();

		PaymentModule paymentModule;
		String requestUrl;
		if (PaymentUtils.isDomesticPayment(paymentType)) {
			paymentModule = paymentProps.getDomestic();
			requestUrl = buildDomesticRequestUrl(order, paymentModule, transactionRef, language);
		} else {
			paymentModule = paymentProps.getInternational();
			requestUrl = buildInternationalRequestUrl(order, paymentModule, transactionRef, language);
		}

		final LocalDateTime now = LocalDateTime.now();
		final Double amount = PaymentUtils.getAmount(paymentProps, paymentModule, order.getTotalPrice());
		final PaymentTransactionEntity transaction = new PaymentTransactionEntity();
		transaction.setPaymentProvider(PaymentConstant.PAYMENT_PROVIDER);
		transaction.setCurrency(order.getCurrency());
		transaction.setAmount(amount);
		transaction.setCreatedDate(now);
		transaction.setRequestId(transactionRef);
		transaction.setTransactionType(PaymentTransactionType.PAY);
		transaction.setPaymentType(paymentType);

		final PaymentTransactionEntryEntity transactionEntry = new PaymentTransactionEntryEntity();
		transactionEntry.setAmount(amount);
		transactionEntry.setCurrency(order.getCurrency());
		transactionEntry.setRequestId(transactionRef);

		transactionEntry.setCreatedDate(now);
		transactionEntry.setTransactionDate(now);
		transactionEntry.setTransactionType(PaymentTransactionEntryType.REQUEST);
		transactionEntry.setRequestData(requestUrl);
		transactionEntry.setVersionId(paymentProps.getVersionModule());

		transaction.addTransactionEntry(transactionEntry);
		order.addPaymentTransaction(transaction);
		order.setPaymentStatus(PaymentStatus.WAITING_PAYMENT);

		orderService.save(order);
		return requestUrl;
	}

	private TransactionStatusInfo validateAndStoreResponsePaymentData(final OrderEntity order,
			final Map<String, Object> fields) {
		final PaymentTransactionEntity transaction = getDefaultTransaction(order);
		if (transaction == null) {
			LOG.info("Transaction for order {} is missing", order.getCode());
			return new TransactionStatusInfo(TransactionStatus.IGNORE, "Transaction is missing");
		}

		TransactionStatusInfo transactionStatusInfo = validateTransactionConsitent(transaction, order.getCode());
		if (transactionStatusInfo != null) {
			return transactionStatusInfo;
		}

		transactionStatusInfo = getTransactionResponseStatus(order, transaction.getPaymentType(), fields);
		if (TransactionStatus.IGNORE.equals(transactionStatusInfo.getStatus())) {
			return transactionStatusInfo;
		}

		addTransactionEntryInfo(order, transaction, fields, transactionStatusInfo);

		return transactionStatusInfo;
	}

	private PaymentTransactionEntity getDefaultTransaction(final OrderEntity order) {
		final Optional<PaymentTransactionEntity> transactionOpt = order.getPaymentTransactions().stream().findFirst()
				.filter(trans -> PaymentTransactionType.PAY.equals(trans.getTransactionType()));
		return transactionOpt.orElse(null);
	}

	private TransactionStatusInfo validateTransactionConsitent(final PaymentTransactionEntity transaction,
			final String orderCode) {
		if (transaction.getEntries().isEmpty()) {
			LOG.info("Transaction entry for order {} is missing", orderCode);
			return new TransactionStatusInfo(TransactionStatus.IGNORE, "Transaction entry request is missing");
		} else if (transaction.getEntries().size() >= 2) {
			LOG.info("Duplicate response data from payment gateway for order: {}", orderCode);
			return new TransactionStatusInfo(TransactionStatus.IGNORE, "Duplicate response data from payment gateway");
		}
		return null;
	}

	private void addTransactionEntryInfo(final OrderEntity order, final PaymentTransactionEntity transaction,
			final Map<String, Object> fields, final TransactionStatusInfo transactionStatusInfo) {
		final String txnResponseCode = PaymentUtils
				.nullToUnknown((String) fields.get(PaymentConstant.VCP_TRANSACTION_RESPONSE_CODE));
		final String amount = PaymentUtils.nullToUnknown((String) fields.get(PaymentConstant.VCP_AMOUNT));
		final String merchTxnRef = PaymentUtils
				.nullToUnknown((String) fields.get(PaymentConstant.VCP_MERCHANT_TRANSACTION_REF));
		final String transactionNo = PaymentUtils
				.nullToUnknown((String) fields.get(PaymentConstant.VCP_TRANSACTION_NO));

		final String orderCode = order.getCode();
		if (TransactionStatus.SUCCESS.equals(transactionStatusInfo.getStatus())) {
			order.setPaymentStatus(PaymentStatus.PAID);
			order.setOrderStatus(OrderStatus.COMPLETED);
		} else if (TransactionStatus.FAIL.equals(transactionStatusInfo.getStatus())) {
			LOG.info("Payment is fail, cancel this order: {}", orderCode);
			order.setPaymentStatus(PaymentStatus.FAIL);
			order.setOrderStatus(OrderStatus.CANCELLED);
		}

		LOG.info("Start adding new transaction entry to store response data for order {}", orderCode);

		final PaymentTransactionEntryEntity transactionEntry = new PaymentTransactionEntryEntity();
		transactionEntry.setAmount(PaymentUtils.getRealAmount(amount));
		transactionEntry.setCurrency(order.getCurrency());
		transactionEntry.setRequestId(merchTxnRef);
		transactionEntry.setTransactionRespCode(txnResponseCode);
		transactionEntry.setTransactionNo(transactionNo);
		transactionEntry.setTransactionStatus(transactionStatusInfo.getStatus().getCode());
		transactionEntry.setTransactionStatusDetail(transactionStatusInfo.getDetail());

		final LocalDateTime now = LocalDateTime.now();
		transactionEntry.setCreatedDate(now);
		transactionEntry.setTransactionDate(now);
		transactionEntry.setTransactionType(PaymentTransactionEntryType.RESPONSE);

		final String responseData = fields.entrySet().stream()
				.map(entry -> StringUtils.join(entry.getKey(), '=', entry.getValue())).collect(Collectors.joining(","));
		transactionEntry.setRequestData(responseData);
		transactionEntry.setVersionId(paymentProps.getVersionModule());

		transaction.addTransactionEntry(transactionEntry);

		order.setModifiedDate(now);
		orderService.save(order);
	}

	private TransactionStatusInfo getTransactionResponseStatus(final OrderEntity order, final PaymentType paymentType,
			final Map<String, Object> fields) {
		if (PaymentUtils.isOrderFinish(order)) {
			LOG.info("Order payment has been cancelled or finished: {}", order.getCode());
			return new TransactionStatusInfo(TransactionStatus.IGNORE, "Order payment has been cancelled or finished");
		}

		final String txnResponseCode = PaymentUtils
				.nullToUnknown((String) fields.get(PaymentConstant.VCP_TRANSACTION_RESPONSE_CODE));
		final String amount = PaymentUtils.nullToUnknown((String) fields.get(PaymentConstant.VCP_AMOUNT));

		final PaymentModule paymentModule = PaymentUtils.isDomesticPayment(paymentType) ? paymentProps.getDomestic()
				: paymentProps.getInternational();
		final String compareAmount = PaymentUtils.getTransactionAmount(paymentProps, paymentModule,
				order.getTotalPrice());

		if (txnResponseCode.equals(TransactionResponseCode.SUCCESS.getCode())) {
			if (compareAmount.equals(amount)) {
				return new TransactionStatusInfo(TransactionStatus.SUCCESS, txnResponseCode, "Payment successful");
			} else {
				// NO PENDING status, set this transaction to fail to fail
				LOG.info("Order {} amount is invalid: response amount {} with db amount {}", order.getCode(), amount,
						compareAmount);
				return new TransactionStatusInfo(TransactionStatus.FAIL, txnResponseCode, "Order amount is invalid");
			}
		} else {
			return new TransactionStatusInfo(TransactionStatus.FAIL, txnResponseCode,
					(String) fields.get(PaymentConstant.VCP_MESSAGE));
		}
	}

	private String buildDomesticRequestUrl(final OrderEntity order, final PaymentModule domestic,
			final String transactionRef, final String language) throws UnsupportedEncodingException {
		final Map<String, Object> requestFields = new HashMap<>();
		requestFields.put(PaymentConstant.VCP_MERCHANT, domestic.getMerchantId());
		requestFields.put(PaymentConstant.VCP_ACCESS_CODE, domestic.getAccessCode());
		requestFields.put(PaymentConstant.VCP_MERCHANT_TRANSACTION_REF, transactionRef);
		requestFields.put(PaymentConstant.VCP_ORDER_INFO, order.getCode());

		final String paymentAmount = PaymentUtils.getTransactionAmount(paymentProps, domestic, order.getTotalPrice());

		requestFields.put(PaymentConstant.VCP_AMOUNT, paymentAmount);
		requestFields.put(PaymentConstant.VCP_RETURN_URL, domestic.getReturnUrl());
		requestFields.put(PaymentConstant.VCP_VERSION, paymentProps.getVersionModule());
		requestFields.put(PaymentConstant.VCP_COMMAND, TransactionCommand.PAY.getCode());
		requestFields.put(PaymentConstant.VCP_LOCALE, PaymentUtils.getDisplayPaymentLanguage(language));
		requestFields.put(PaymentConstant.VCP_CURRENCY, commerceProps.getCurrency());
		requestFields.put(PaymentConstant.TITLE, paymentProps.getTitle());

		if (StringUtils.isNotEmpty(domestic.getSecretKey())) {
			final String requestUrl = PaymentUtils.appendAndHashParams(domestic.getUrl(), requestFields,
					domestic.getSecretKey());
			LOG.info("Request URL: {}", requestUrl);
			return requestUrl;
		}
		return null;
	}

	private String buildInternationalRequestUrl(final OrderEntity order, final PaymentModule international,
			final String transactionRef, final String language) throws UnsupportedEncodingException {
		final Map<String, Object> requestFields = new HashMap<>();
		requestFields.put(PaymentConstant.VCP_MERCHANT, international.getMerchantId());
		requestFields.put(PaymentConstant.VCP_ACCESS_CODE, international.getAccessCode());
		requestFields.put(PaymentConstant.VCP_MERCHANT_TRANSACTION_REF, transactionRef);
		requestFields.put(PaymentConstant.VCP_ORDER_INFO, order.getCode());

		final String paymentAmount = PaymentUtils.getTransactionAmount(paymentProps, international,
				order.getTotalPrice());

		requestFields.put(PaymentConstant.VCP_AMOUNT, paymentAmount);
		requestFields.put(PaymentConstant.VCP_RETURN_URL, international.getReturnUrl());
		requestFields.put(PaymentConstant.VCP_VERSION, paymentProps.getVersionModule());
		requestFields.put(PaymentConstant.VCP_COMMAND, TransactionCommand.PAY.getCode());
		requestFields.put(PaymentConstant.VCP_LOCALE, PaymentUtils.getDisplayPaymentLanguage(language));
		requestFields.put(PaymentConstant.TITLE, paymentProps.getTitle());
		requestFields.put(PaymentConstant.AGAIN_LINK, international.getReturnUrl());

		if (StringUtils.isNotEmpty(international.getSecretKey())) {
			final String requestUrl = PaymentUtils.appendAndHashParams(international.getUrl(), requestFields,
					international.getSecretKey());
			LOG.info("Request URL: {}", requestUrl);
			return requestUrl;
		}

		return null;
	}

	private boolean hasReturnData(final String data) {
		return !PaymentConstant.NO_VALUE_RETURN.equals(data);
	}

}
