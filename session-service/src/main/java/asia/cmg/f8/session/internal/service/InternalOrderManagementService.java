package asia.cmg.f8.session.internal.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import asia.cmg.f8.commerce.OrderCompletedEvent;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.event.OrderCompletedEventUtil;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.service.OrderManagementService;
import asia.cmg.f8.session.service.SessionPackageManagementService;

/**
 * Created on 12/22/16.
 */
@Service
public class InternalOrderManagementService implements OrderManagementService {
    public static final Logger LOG = LoggerFactory.getLogger(InternalOrderManagementService.class);

    private final OrderRepository repository;

    private final SessionPackageManagementService sessionPackageManagementService;


    @Autowired
    public InternalOrderManagementService(final OrderRepository repository,
            final SessionPackageManagementService sessionPackageManagementService) {
        this.repository = repository;
        this.sessionPackageManagementService = sessionPackageManagementService;
    }

    @Transactional
    @Override
    public OrderEntity createIfNotExist(final OrderEntity entity) {

        final String uuid = entity.getUuid();
        final Optional<OrderEntity> orderEntity = repository.findOneByUuid(uuid);

        if (orderEntity.isPresent()) {
            return orderEntity.get();
        }
        return repository.save(entity);
    }

    @Override
    public Optional<OrderEntity> findOneByUuid(final String uuid) {
        return repository.findOneByUuid(uuid);
    }
    
    @Override
    public Optional<OrderEntity> findOneBySessionPackageUuid(final String packageId) {
        return repository.findOneBySessionPackageUuid(packageId);
    }

    @Override
    public OrderEntity saveOrderEntity(final OrderEntity orderEntity) {
        return repository.save(orderEntity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOrderSetupPackage(final OrderCompletedEvent event) {
        final String userUuid = String.valueOf(event.getUserUuid());
        // create order
        final OrderEntity order = this.createIfNotExist(OrderCompletedEventUtil.create(event));

        // setup session package
        final SessionPackageEntity packageEntity = sessionPackageManagementService
                .setupPackage(order);
        if (packageEntity == null) {
            // something wrong happen. We cancel this process.
            LOG.warn("A session package existed with the order {} and user {}", order.getUuid(),
                    userUuid);
        } else {
            LOG.info("Setup session package {}, order {} and package status {} - SUCCESS.",
                    userUuid, packageEntity.getOrderUuid(), packageEntity.getStatus());
        }
    }
}
