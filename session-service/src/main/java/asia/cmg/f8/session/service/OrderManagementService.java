package asia.cmg.f8.session.service;

import java.util.Optional;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.session.entity.OrderEntity;

/**
 * Created on 12/22/16.
 */
public interface OrderManagementService {

    /**
     * Create new {@link OrderEntity} if it's not existed. If the order existed, it will return persisted order.
     *
     * @param entity the order entity.
     * @return created order. Method should not return null.
     */
    OrderEntity createIfNotExist(OrderEntity entity);

    Optional<OrderEntity> findOneByUuid(String sessionPackageUuid);
    
    Optional<OrderEntity> findOneBySessionPackageUuid(final String packageId);

    OrderEntity saveOrderEntity(OrderEntity orderEntity);

    void createOrderSetupPackage(OrderCompletedEvent event);
}
