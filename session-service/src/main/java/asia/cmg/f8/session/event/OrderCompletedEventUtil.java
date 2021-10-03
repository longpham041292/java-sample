package asia.cmg.f8.session.event;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.common.spec.commerce.ProductTrainingStyle;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.entity.OrderEntity;

import java.time.LocalDate;

/**
 * Created on 12/22/16.
 */
public final class OrderCompletedEventUtil {

    private OrderCompletedEventUtil() {
        // empty
    }

    public static OrderEntity create(final OrderCompletedEvent event) {
        final OrderEntity order = new OrderEntity();
        order.setCommission(event.getCommission());
        order.setCurrency(stringOf(event.getCurrency()));
        order.setNumOfSessions(event.getNumberOfSession());
        order.setOrderCode(stringOf(event.getOrderCode()));
        order.setOrderDate(ZoneDateTimeUtils.convertFromUTCToLocalDateTime(event.getCreatedDate()));
        order.setPrice(event.getPackagePrice());
        order.setPtUuid(stringOf(event.getPtUuid()));
        order.setUserUuid(stringOf(event.getUserUuid()));
        order.setUuid(stringOf(event.getOrderId()));
        order.setNumberOfLimitDay(event.getExpireLimit());
        order.setProductUuid(stringOf(event.getProductUuid()));
        order.setProductName(stringOf(event.getProductName()));
        order.setFreeOrder(event.getFreeOrder());
        order.setContractNumber(stringOf(event.getContractNumber()));
        order.setOriginalPrice(event.getOriginalPrice());
        order.setDiscount(event.getDiscount());
        order.setOrderClubcode(stringOf(event.getClubcode()));
        order.setTrainingStyle(ProductTrainingStyle.valueOf(stringOf(event.getTrainingStyle())));
        if (event.getFreeOrder()) {
            order.setExpiredDate(LocalDate.now().atTime(23, 59, 59)
                    .plusDays(event.getExpireLimit()));
        }
        return order;
    }

    /**
     * Convert a char sequence to string. if the char sequence is null then return NULL value instead of "null" string.
     *
     * @param sequence the char sequence
     * @return string or null
     */
    private static String stringOf(final CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return String.valueOf(sequence);
    }
}
