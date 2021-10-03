package asia.cmg.f8.session.internal.service;

import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.CreatedSessionPackageEvent;
import asia.cmg.f8.session.TransferSessionPackageEvent;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import asia.cmg.f8.session.event.CreatedPackageHandler;
import asia.cmg.f8.session.event.EventHandler;
import asia.cmg.f8.session.repository.SessionPackageRepository;
import asia.cmg.f8.session.service.SessionManagementService;
import asia.cmg.f8.session.service.SessionPackageManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created on 12/22/16.
 */
@Service
public class InternalSessionPackageManagementService implements SessionPackageManagementService {

    public static final Logger LOGGER = LoggerFactory.getLogger(InternalSessionPackageManagementService.class);

    private final SessionPackageRepository packageRepository;

    private final SessionManagementService sessionManagementService;

    private final EventHandler eventHandler;

    private final CreatedPackageHandler createdPackageHandler;

    @Autowired
    public InternalSessionPackageManagementService(final SessionPackageRepository packageRepository,
                                                   final SessionManagementService sessionManagementService,
                                                   final EventHandler eventHandler,
                                                   final CreatedPackageHandler createdPackageHandler) {
        this.packageRepository = packageRepository;
        this.sessionManagementService = sessionManagementService;
        this.eventHandler = eventHandler;
        this.createdPackageHandler = createdPackageHandler;
    }

    @Override
    public List<Object[]> getSessionPackageEntitiesbyUser(final String userUuid, final long expiredDate, final SessionPackageStatus differentStatus){
        return packageRepository.getSessionPackageEntitiesbyUser(userUuid, expiredDate, differentStatus);
    }
    @Override
    @Transactional
    public SessionPackageEntity setupPackage(final OrderEntity order) {

        if (hasPackage(order.getUuid())) {
            // something wrong.
            return null;
        }

        final SessionPackageEntity sessionPackageEntity = packageRepository.save(createPackage(order));

        final String packageUuid = sessionPackageEntity.getUuid();
        LOGGER.info("Created session package for order \"{}\", user \"{}\", package \"{}\"", order.getUuid(), order.getUserUuid(), packageUuid);

        final List<SessionEntity> sessions = sessionManagementService.setupSession(sessionPackageEntity);
        if (sessions == null || sessions.size() != sessionPackageEntity.getNumOfSessions()) {
            final int count = sessions == null ? 0 : sessions.size();
            // something wrong.
            LOGGER.error("Expect {} sessions are created but only {} for order {}", sessionPackageEntity.getNumOfSessions(), count, packageUuid);

            throw new IllegalStateException("Error on setup session for order " + sessionPackageEntity.getOrderUuid());
        }

        //Fire created session package event
        final CreatedSessionPackageEvent event = new CreatedSessionPackageEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setSubmittedAt(System.currentTimeMillis());
        event.setPackageUuid(packageUuid);
        event.setCreatedDate(
                ZoneDateTimeUtils.convertToSecondUTC(sessionPackageEntity.getCreatedDate()));
        event.setPtUuid(sessionPackageEntity.getPtUuid());
        event.setUserUuid(sessionPackageEntity.getUserUuid());
        createdPackageHandler.publish(event);
        LOGGER.debug("Fired Created Session Package event for package uuid {}", packageUuid);

        return sessionPackageEntity;
    }

    @Transactional
    @Override
    public Optional<SessionPackageEntity> transferPackage(final SessionPackageEntity sessionPackage,
                                                          final String newTrainer) {
        if (sessionPackage.getPtUuid().equalsIgnoreCase(newTrainer)) {
            throw new IllegalArgumentException("Can not transfer this package to the trainer who already involved in this package");
        }

        final SessionPackageEntity newPackage = createPackage(sessionPackage);
        // Set new trainer
        newPackage.setPtUuid(newTrainer);

        final List<SessionEntity> sessions = sessionManagementService.transferSession(sessionPackage, newPackage);
        if (!sessions.isEmpty()) {

            // update old package to INVALID
            invalidPackage(sessionPackage);

            // change status of new package to TRANSFERRED
            newPackage.setStatus(SessionPackageStatus.TRANSFERRED);

            // persist new package
            final Optional<SessionPackageEntity> opSavedSessionPackage = Optional.ofNullable(packageRepository.save(newPackage));

            opSavedSessionPackage.ifPresent(sessionPackageEntity -> fireTransferPackageEvent(sessionPackageEntity, sessionPackage.getUuid(), sessionPackage.getPtUuid()));

            return opSavedSessionPackage;
        }
        return Optional.empty();
    }

    private void fireTransferPackageEvent(final SessionPackageEntity sessionPackage,
                                          final String oldSessionPackageUuid,
                                          final String oldTrainer) {
        final TransferSessionPackageEvent event = TransferSessionPackageEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setOldSessionPackageUuid(oldSessionPackageUuid)
                .setNewSessionPackageUuid(sessionPackage.getUuid())
                .setUserUuid(sessionPackage.getUserUuid())
                .setOldTrainerUuid(oldTrainer)
                .setNewTrainerUuid(sessionPackage.getPtUuid())
                .setSubmittedAt(System.currentTimeMillis())
                .build();

        eventHandler.publish(event);
        LOGGER.info("Fired event session transfer from userid {} to userid {} ", oldTrainer, sessionPackage.getPtUuid());
    }

    @Override
    public Optional<SessionPackageEntity> getSessionPackageBySessionUUID(final String sessionUuid) {
        return packageRepository.getSessionPackageBySessionUuid(sessionUuid);
    }

	@Override
	public Optional<SessionPackageEntity> getSessionPackageBySessionPackageUuid(final String sessionUuid) {
		return packageRepository.getSessionPackageBySessionPackageUuid(sessionUuid);
	}

    @Override
    public SessionPackageEntity saveSessionPackage(final SessionPackageEntity sessionPackageEntity) {
        return packageRepository.save(sessionPackageEntity);
    }

    @Override
    public Optional<SessionPackageEntity> getTransferableSessionPackageByUuid(final String packageUuid) {
        return packageRepository.getTransferableSessionPackageByUuid(packageUuid, SessionPackageStatus.getValidSessionPackageStatus());
    }

    @Override
    public Optional<SessionPackageEntity> getSessionPackageByUuid(final String packageUUID) {
        return packageRepository.getSessionPackageByUuid(packageUUID);
    }

    @Override
    public Optional<CurrentTransferPackageInfo> getTransferSessionPackageInfo(final String oldPackageUuid, final String newPackageUuid) {
        final List<Object[]> transferSessionPackageInfo = packageRepository.getTransferSessionPackageInfo(oldPackageUuid, newPackageUuid);
        if (!transferSessionPackageInfo.isEmpty()) {
            final Object[] transferInfo = transferSessionPackageInfo.get(0);
            final CurrentTransferPackageInfo sessionPackageInfo = new CurrentTransferPackageInfo();
            sessionPackageInfo.setOldPackageUuid((String) transferInfo[0]);
            sessionPackageInfo.setNewPackageUuid((String) transferInfo[1]);
            sessionPackageInfo.setUserUuid((String) transferInfo[2]);
            sessionPackageInfo.setUserFullName((String) transferInfo[3]);
            sessionPackageInfo.setOldTrainerUuid((String) transferInfo[4]);
            sessionPackageInfo.setOldTrainerFullName((String) transferInfo[5]);
            sessionPackageInfo.setNewTrainerUuid((String) transferInfo[6]);
            sessionPackageInfo.setNewTrainerFullName((String) transferInfo[7]);

            final Timestamp endTimestamp = (Timestamp) transferInfo[8];
            final Long expiredDate = (endTimestamp != null) ? endTimestamp.toInstant().getEpochSecond() : null;
            sessionPackageInfo.setOrderExpiredDate(expiredDate);

            sessionPackageInfo.setNumberOfBurnedSessions((Integer) transferInfo[9]);
            sessionPackageInfo.setNumberOfTotalSessions((Integer) transferInfo[10]);

            sessionPackageInfo.setUserEmail((String) transferInfo[11]);
            sessionPackageInfo.setOldTrainerEmail((String) transferInfo[12]);
            sessionPackageInfo.setNewTrainerEmail((String) transferInfo[13]);
            final Timestamp createdTimestamp = (Timestamp) transferInfo[14];
            final Long createdDate = (createdTimestamp != null) ? createdTimestamp.toInstant().toEpochMilli() : null;
            sessionPackageInfo.setCreatedDate(createdDate);
            final Timestamp modifiedTimestamp = (Timestamp) transferInfo[15];
            final Long modifiedDate = (modifiedTimestamp != null) ? modifiedTimestamp.toInstant().toEpochMilli() : null;
            sessionPackageInfo.setModifiedDate(modifiedDate);

            return Optional.of(sessionPackageInfo);
        }
        return Optional.empty();
    }

	/***
	 * update session package status
	 */
	@Transactional
	public void updateSessionPackageStatus(String packageSessionUuid, SessionPackageStatus status) {
		packageRepository.updateSessionPackageStatus(packageSessionUuid, status.toString());
	}

    private void invalidPackage(final SessionPackageEntity packageEntity) {
        packageEntity.setStatus(SessionPackageStatus.INVALID);
        packageRepository.save(packageEntity);
    }

    /**
     * Create new {@link SessionPackageEntity} from given {@link OrderEntity}. The new created will have
     * <ul>
     * <li>Default status is {@link SessionPackageStatus#VALID}</li>
     * <li>Default number of burned sessions is 0</li>
     * </ul>
     *
     * @param order the order.
     * @return the {@link SessionPackageEntity}
     */
    private SessionPackageEntity createPackage(final OrderEntity order) {
        final SessionPackageEntity packageEntity = new SessionPackageEntity();
        packageEntity.setUuid(UUID.randomUUID().toString());
        packageEntity.setOrderUuid(order.getUuid());
        packageEntity.setNumOfSessions(order.getNumOfSessions());
        packageEntity.setStatus(SessionPackageStatus.VALID); // default status.
        packageEntity.setUserUuid(order.getUserUuid());
        packageEntity.setPtUuid(order.getPtUuid());
        packageEntity.setNumOfBurned(0);
        return packageEntity;
    }

    /**
     * Create a new {@link SessionPackageEntity} from given {@link SessionPackageEntity}. The new created will have
     * <ul>
     * <li>Default status is {@link SessionPackageStatus#VALID}</li>
     * <li>Number of burned sessions are transferred from the old one</li>
     * </ul>
     *
     * @param packageEntity the package
     * @return the new {@link SessionPackageEntity}
     */
    private SessionPackageEntity createPackage(final SessionPackageEntity packageEntity) {
        final SessionPackageEntity sessionPackageEntity = new SessionPackageEntity();
        sessionPackageEntity.setUuid(UUID.randomUUID().toString());
        sessionPackageEntity.setNumOfBurned(packageEntity.getNumOfBurned());
        sessionPackageEntity.setNumOfSessions(packageEntity.getNumOfSessions());
        sessionPackageEntity.setOrderUuid(packageEntity.getOrderUuid());
        sessionPackageEntity.setPtUuid(packageEntity.getPtUuid());
        sessionPackageEntity.setUserUuid(packageEntity.getUserUuid());
        sessionPackageEntity.setStatus(SessionPackageStatus.VALID);
        return sessionPackageEntity;
    }

    private boolean hasPackage(final String orderUuid) {
        return packageRepository.countPackage(orderUuid) > 0;
    }
}
