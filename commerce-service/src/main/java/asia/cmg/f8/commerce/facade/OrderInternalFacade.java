package asia.cmg.f8.commerce.facade;

import asia.cmg.f8.commerce.dto.Order;
import asia.cmg.f8.commerce.dto.OrderBasicInfo;
import asia.cmg.f8.commerce.dto.OrderEntry;
import asia.cmg.f8.commerce.entity.OrderEntity;
import asia.cmg.f8.commerce.entity.OrderEntryEntity;
import asia.cmg.f8.commerce.service.OrderService;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class OrderInternalFacade {

    //
    private final OrderService orderService;

    @Inject
    public OrderInternalFacade(final OrderService orderService){
    
    	this.orderService = orderService;
    }
    
    public Order getOrderByUuid(final String orderUuid) {
        final OrderEntity order = orderService.getOrder(orderUuid);
        if (order == null) {
            throw new IllegalArgumentException(String.format("Order not found uuid %s", orderUuid));
        }

        final List<OrderEntry> products = order
                .getOrderProductEntries()
                .stream()
                .map(entity -> OrderEntry.builder().numOfSessions(entity.getQuantity())
                        .unitPrice(entity.getUnitPrice()).entryNumber(entity.getEntryNumber())
                        .expireLimit(entity.getExpireLimit()).subTotal(entity.getSubTotal())
                        .totalPrice(entity.getTotalPrice()).build()).collect(Collectors.toList());

        return Order.builder().id(order.getUuid()).code(order.getCode())
                .currency(order.getCurrency()).id(order.getUuid())
                .createdTime(ZoneDateTimeUtils.convertToSecondUTC(order.getCreatedDate()))
                .orderStatus(order.getOrderStatus()).paymentStatus(order.getPaymentStatus())
                .subTotal(order.getSubTotal()).totalPrice(order.getTotalPrice())
                .ptUuid(order.getPtUuid()).userUuid(order.getUserUuid()).addAllProducts(products)
                .freeOrder(order.getFreeOrder())
                .build();
    }


    public List<OrderBasicInfo> searchNewOrder(final String clientUuid, final String ptUuid,
            final LocalDateTime fromDate) {
        final List<OrderEntity> newOrders = orderService.searchNewOrders(clientUuid, ptUuid,
                fromDate);
        return newOrders
                .stream()
                .map(order -> {
                    final OrderEntryEntity orderEntry = order.getOrderProductEntries().get(0);
                    return OrderBasicInfo
                            .builder()
                            .id(order.getUuid())
                            .orderCode(order.getCode())
                            .createdDate(
                                    ZoneDateTimeUtils.convertToSecondUTC(order.getCreatedDate()))
                                    .numberOfSession(orderEntry.getQuantity())
                                    .expireLimit(orderEntry.getExpireLimit())
                                    .packagePrice(order.getTotalPrice())
                                    .commision(orderEntry.getCommision()).currency(order.getCurrency())
                                    .userUuid(order.getUserUuid()).ptUuid(order.getPtUuid()).build();
                }).collect(Collectors.toList());
    }

}
