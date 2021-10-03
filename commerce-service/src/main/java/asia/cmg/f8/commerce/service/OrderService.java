package asia.cmg.f8.commerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.config.CommerceProperties;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderSubscriptionEntryEntity;
import asia.cmg.f8.commerce.facade.OrderFacade;
import asia.cmg.f8.commerce.repository.OrderRepository;
import asia.cmg.f8.common.spec.order.OrderStatus;
import asia.cmg.f8.common.spec.order.PaymentStatus;

@Service
@Transactional
public class OrderService {

	private static final Logger LOG = LoggerFactory.getLogger(OrderFacade.class);

	// @Inject
	private final OrderRepository orderRepository;

	// @Inject
	private final CommerceProperties commerceProps;

	@Inject
	public OrderService(final OrderRepository orderRepository, final CommerceProperties commerceProps) {

		this.orderRepository = orderRepository;
		this.commerceProps = commerceProps;
	}

	public void createOrder(final OrderEntity order) {
		orderRepository.save(order);
	}

	public void save(final OrderEntity order) {
		orderRepository.save(order);
	}

	/**
	 * Get order by internal service.
	 * 
	 * @param orderUuid order uuid
	 * @return OrderEntity
	 */
	@Transactional(readOnly = true)
	public OrderEntity getOrder(final String orderUuid) {
		final Optional<OrderEntity> orderOpt = orderRepository.findOrderByUuid(orderUuid);
		if (orderOpt.isPresent()) {
			return orderOpt.get();
		}
		return null;
	}

	/**
	 * Get order with transactions by internal service.
	 * 
	 * @param orderUuid order uuid
	 * @return OrderEntity
	 */
	@Transactional(readOnly = true)
	public OrderEntity getOrderWithTransactions(final String orderUuid) {
		final Optional<OrderEntity> orderOpt = orderRepository.findOrderByUuid(orderUuid);
		if (orderOpt.isPresent()) {
			final OrderEntity order = orderOpt.get();
			order.getPaymentTransactions().size();
			return order;
		}
		return null;
	}

	/**
	 * Get order by internal service.
	 * 
	 * @param code order code
	 * @return OrderEntity
	 */
	@Transactional(readOnly = true)
	public OrderEntity getOrderByCode(final String code) {
		final Optional<OrderEntity> orderOpt = orderRepository.findOrderByCode(code);
		if (orderOpt.isPresent()) {
			return orderOpt.get();
		}
		return null;
	}

	/**
	 * Get order by user uuid.
	 * 
	 * @param orderUuid order uuid
	 * @return OrderEntity
	 */
	@Transactional(readOnly = true)
	public OrderEntity getOrderByUser(final String orderUuid, final String userUuid) {
		final Optional<OrderEntity> orderOpt = orderRepository.findOrderByUserUuid(orderUuid, userUuid);
		if (orderOpt.isPresent()) {
			return orderOpt.get();
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<Object[]> getOrderHistoryByUser(final String userUuid) {
		final List<String> status = new ArrayList<>(2);
		status.add(PaymentStatus.PAID.toString());
		// Only return paid payment to the history list
		// status.add(PaymentStatus.WAITING_PAYMENT);
		return orderRepository.findOrdersByStatus(userUuid, status);
	}

	public String getOrderPaymentStatus(final String orderUuid, final String userUuid) {
		final Optional<PaymentStatus> status = orderRepository.findStatusOfOrder(orderUuid, userUuid);
		if (status.isPresent()) {
			return status.get().name().toLowerCase(Locale.US);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<String> getOrderByPaymentStatus(final PaymentStatus paymentStatus, final int queryAfter) {
		final LocalDateTime toDate = LocalDateTime.now().minusMinutes(queryAfter);
		return orderRepository.findOrdersWithPaymentStatus(paymentStatus, toDate);
	}

	/**
	 * Get new, complete orders by internal service.
	 * 
	 * @param clientUuid buyer uuid
	 * @param ptUuid     pt uuid
	 * @param fromDate   from date
	 * @return list of new orders
	 */
	@Transactional(readOnly = true)
	public List<OrderEntity> searchNewOrders(final String clientUuid, final String ptUuid,
			final LocalDateTime fromDate) {
		List<OrderEntity> newOrders;
		if (fromDate != null) {
			newOrders = orderRepository.findNewOrdersByStatusAndDate(clientUuid, ptUuid, fromDate,
					OrderStatus.COMPLETED, PaymentStatus.PAID);
		} else {
			newOrders = orderRepository.findNewOrdersByStatus(clientUuid, ptUuid, OrderStatus.COMPLETED,
					PaymentStatus.PAID);
		}

		return newOrders;
	}

	/**
	 * Verify if user has pending orders with PT.
	 * 
	 * @param clientUuid buyer uuid
	 * @param ptUuid     pt uuid
	 * @return check pending result
	 */
	public boolean hasPendingOrderWithPt(final String clientUuid, final String ptUuid) {
		final LocalDateTime fromDate = LocalDateTime.now().minusHours(commerceProps.getHoursGracePeriod());
		final Integer count = orderRepository.findPendingOrderWithPt(clientUuid, ptUuid, fromDate,
				PaymentStatus.WAITING_PAYMENT);
		return count > 0;
	}

	/**
	 * Cancel order pending orders with current PT.
	 * 
	 * @param clientUuid buyer uuid
	 * @param ptUuid     pt uuid
	 */
	public void cancelPendingOrdersWithPt(final String clientUuid, final String ptUuid) {
		final LocalDateTime fromDate = LocalDateTime.now().minusHours(commerceProps.getHoursGracePeriod());
		final int count = orderRepository.cancelPendingOrdersWithPt(clientUuid, ptUuid, fromDate,
				PaymentStatus.WAITING_PAYMENT, OrderStatus.CANCELLED, PaymentStatus.FAIL);
		LOG.info("{} orders has been cancel for user {} with pt {}", count, clientUuid, ptUuid);
	}

	public OrderEntity update(final OrderEntity entity) {
		return orderRepository.save(entity);
	}

	@Transactional
	public void updateContractUploadStatus(final List<String> contractNumbers) {
		orderRepository.updateContractUploadStatus(contractNumbers);
	}

	@Transactional
	public void updateOrderExpiredDate(final LocalDate expiredDate, String uuid) {
		LocalDateTime endTime = LocalDateTime.of(expiredDate.getYear(), expiredDate.getMonth(),
				expiredDate.getDayOfMonth(), 23, 59, 59);
		LOG.info("update expired date: " + endTime.toString());
		orderRepository.updateOrderExpiredDate(endTime, uuid);
	}

	/**
	 * Get OrderSubscriptionEntries by order id.
	 * 
	 * @param order id
	 * @return List<OrderSubscriptionEntryEntity>
	 */
	@Transactional(readOnly = true)
	public List<OrderSubscriptionEntryEntity> getOrderSubcriptionEntriesByOrderId(final long orderId) {
		final List<OrderSubscriptionEntryEntity> orderSubscriptionEntries = orderRepository.findOrderSubscriptionEntriesByOrderId(orderId);
		if(orderSubscriptionEntries != null & orderSubscriptionEntries.size() > 0)
			return orderSubscriptionEntries;
		return null;
	}

	public Boolean checkSuccessOrdered(String uuid) {
		List<PaymentStatus> status = Arrays.asList(PaymentStatus.PAID);
		Integer number = orderRepository.checkSuccessOrdered(uuid, status);
		return (number == null || number <= 0 ) ? false : true;
	}
}
