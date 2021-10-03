package asia.cmg.f8.notification.client;

import asia.cmg.f8.notification.dto.Order;
import org.springframework.stereotype.Component;

@Component
public class CommerceClientFallbackImpl implements CommerceClient {

    @Override
    public Order getOrder(final String orderUuid) {
        return null;
    }

}
