package asia.cmg.f8.session.internal.service;

import static asia.cmg.f8.session.entity.SessionAction.CREATED;
import static asia.cmg.f8.session.entity.SessionAction.TRANSFER;
import static asia.cmg.f8.session.entity.SessionStatus.CANCELLED;
import static asia.cmg.f8.session.entity.SessionStatus.OPEN;
import static asia.cmg.f8.session.entity.SessionStatus.PENDING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionAction;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionHistoryEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.exception.BookingErrorCode;
import asia.cmg.f8.session.exception.BookingValidationException;
import asia.cmg.f8.session.repository.OrderRepository;
import asia.cmg.f8.session.repository.SessionPackageRepository;
import asia.cmg.f8.session.repository.SessionRepository;
import asia.cmg.f8.session.service.EventManagementService;
import asia.cmg.f8.session.service.SessionHistoryManagementService;
import asia.cmg.f8.session.service.SessionManagementService;
import asia.cmg.f8.session.wrapper.dto.AvailableSessionWithOrder;
import asia.cmg.f8.session.wrapper.service.OrderSessionWrapperService;

/**
 * Created on 12/22/16.
 */
@Service
@SuppressWarnings("PMD.ExcessiveImports")
public class InternalSessionManagementService implements SessionManagementService {

    public static final Logger LOGGER = LoggerFactory.getLogger(InternalSessionManagementService.class);

    private static final List<SessionStatus> ALLOW_TO_TRANSFER_STATUS = Arrays.asList(OPEN, CANCELLED, PENDING);

    private final SessionRepository sessionRepository;
    private final SessionHistoryManagementService sessionHistoryManagementService;
    private final EventManagementService eventManagementService;
    private final SessionPackageRepository packageRepository;
    private final OrderRepository orderRepository;
    private final OrderSessionWrapperService orderSessionWrapperService;

    @Autowired
    public InternalSessionManagementService(final SessionRepository sessionRepository,
                                            final SessionHistoryManagementService sessionHistoryManagementService,
                                            final EventManagementService eventManagementService,
                                            final SessionPackageRepository packageRepository,
                                            final OrderRepository orderRepository,
                                            final OrderSessionWrapperService orderSessionWrapperService) {
        this.sessionRepository = sessionRepository;
        this.sessionHistoryManagementService = sessionHistoryManagementService;
        this.eventManagementService = eventManagementService;
        this.packageRepository = packageRepository;
        this.orderRepository = orderRepository;
        this.orderSessionWrapperService = orderSessionWrapperService;
    }

    @Transactional
    @Override
    public List<SessionEntity> setupSession(final SessionPackageEntity packageEntity) {

        final int numOfSessions = packageEntity.getNumOfSessions();
        final List<SessionEntity> entities = new ArrayList<>(numOfSessions);
        final List<SessionHistoryEntity> historyEntities = new ArrayList<>(numOfSessions);
        for (int i = 0; i < numOfSessions; i++) {
            final SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setLastStatus(OPEN);
            sessionEntity.setLastStatusModifiedDate(LocalDateTime.now());
            sessionEntity.setStatus(OPEN);
            sessionEntity.setStatusModifiedDate(LocalDateTime.now());
            sessionEntity.setStartTime(LocalDateTime.now());
            sessionEntity.setPackageUuid(packageEntity.getUuid());
            sessionEntity.setPtUuid(packageEntity.getPtUuid());
            sessionEntity.setUserUuid(packageEntity.getUserUuid());
            sessionEntity.setUuid(UUID.randomUUID().toString());
            entities.add(sessionEntity);

            // Log history
            final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
            sessionHistoryEntity.setSessionUuid(sessionEntity.getUuid());
            sessionHistoryEntity.setUserUuid(sessionEntity.getUserUuid());
            sessionHistoryEntity.setNewPackageUuid(sessionEntity.getPackageUuid());
            sessionHistoryEntity.setOldPackageUuid(sessionEntity.getPackageUuid());
            sessionHistoryEntity.setOldPtUuid(sessionEntity.getPtUuid());
            sessionHistoryEntity.setNewPtUuid(sessionEntity.getPtUuid());
            sessionHistoryEntity.setNewStatus(sessionEntity.getStatus());
            sessionHistoryEntity.setAction(CREATED);

            historyEntities.add(sessionHistoryEntity);
        }
        sessionRepository.save(entities);
        sessionHistoryManagementService.save(historyEntities);

        LOGGER.info("Created {} sessions for package {} of order {}", entities.size(), packageEntity.getUuid(), packageEntity.getOrderUuid());
        return entities;
    }

    @Transactional
    @Override
    public List<SessionEntity> transferSession(final SessionPackageEntity oldPackage, final SessionPackageEntity newPackage) {

        final List<SessionEntity> sessionEntities = this.sessionRepository.filterByPackageAndStatus(oldPackage.getUuid(),
                SessionStatus.getTransferableSessionStatus(), SessionStatus.getBookingStatus());
        if (sessionEntities != null) {
            final int totalOfSession = oldPackage.getNumOfSessions();
            if (sessionEntities.size() > totalOfSession) {
                throw new BookingValidationException(
                        BookingErrorCode.TRANSFER_EXCEED_SESSION.withDetail(String.format(
                                "Transfer %d session while total of session is %d",
                                sessionEntities.size(), totalOfSession)));
            }

            LOGGER.info("Start transfer {} sessions from old package {} to new package {}",
                    sessionEntities.size(), oldPackage.getUuid(), newPackage.getUuid());

            final List<String> deletedEvents = new ArrayList<>();
            for (final SessionEntity entity : sessionEntities) {
                final SessionStatus oldStatus = entity.getStatus();
                final String oldPackageUuid = entity.getPackageUuid();
                final String oldPtUuid = entity.getPtUuid();

                //Delete events when session are pending and confirmed status.
                deletedEvents.add(entity.getPtEventId());
                deletedEvents.add(entity.getUserEventId());

                entity.setPackageUuid(newPackage.getUuid());
                entity.setPtUuid(newPackage.getPtUuid());
                entity.setLastStatus(oldStatus);
                entity.setLastStatusModifiedDate(entity.getStatusModifiedDate());
                entity.setStatus(SessionStatus.TRANSFERRED);
                entity.setStatusModifiedDate(LocalDateTime.now());

                // Log history
                final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
                sessionHistoryEntity.setSessionUuid(entity.getUuid());
                sessionHistoryEntity.setUserUuid(entity.getUserUuid());
                sessionHistoryEntity.setNewPackageUuid(entity.getPackageUuid());
                sessionHistoryEntity.setOldPackageUuid(oldPackageUuid);
                sessionHistoryEntity.setOldPtUuid(oldPtUuid);
                sessionHistoryEntity.setNewPtUuid(entity.getPtUuid());
                sessionHistoryEntity.setOldStatus(oldStatus);
                sessionHistoryEntity.setNewStatus(entity.getStatus());
                sessionHistoryEntity.setAction(TRANSFER);

                sessionHistoryManagementService.save(sessionHistoryEntity);
            }

            if (!deletedEvents.isEmpty()) {
                eventManagementService.deleteEvents(deletedEvents);
            }

            return sessionRepository.save(sessionEntities);
        }
        return Collections.emptyList();
    }

    @Override
    public List<SessionStatus> getValidStatusForTransfer() {
        return Collections.unmodifiableList(ALLOW_TO_TRANSFER_STATUS);
    }

    @Override
    public SessionEntity save(final SessionEntity sessionEntity) {
        return this.sessionRepository.save(sessionEntity);
    }


    @Override
    public List<AvailableSessionWithOrder> getAvailableSessions(final String userId,
                                                                final String trainerId) {
        final List<String> availableStatus = SessionStatus.getBookableSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        return orderSessionWrapperService.getAvailableSessionWithOrder(userId, trainerId,
                availableStatus);
    }
    
    @Override
    public List<AvailableSessionWithOrder> getAvailableSessionsByPackageId(final String userId, final String packageId) {
        final List<String> availableStatus = SessionStatus.getBookableSessionStatus().stream()
                .map(Enum::name).collect(Collectors.toList());
        return orderSessionWrapperService.getAvailableSessionWithOrderByPackageId(userId, packageId, availableStatus);
        
    }

    @Override
    @Transactional
    public List<SessionEntity> autoExpiredSession() {
        LOGGER.info("Process auto expiration for sessions");
        final List<SessionEntity> expiredSessions = this.sessionRepository.getExpiredSession(SessionStatus.getExpirableSessionStatus());
        if (!expiredSessions.isEmpty()) {

            LOGGER.info("Found {} expired sessions", expiredSessions.size());
            final List<SessionHistoryEntity> historyEntities = new ArrayList<>(expiredSessions.size());
            final List<String> updateEventList = new ArrayList<>(expiredSessions.size());
            final List<String> deleteEventList = new ArrayList<>(expiredSessions.size());

            expiredSessions.forEach(session -> {
                // Change status to expired for all session
                session.setLastStatus(session.getStatus());
                session.setLastStatusModifiedDate(session.getStatusModifiedDate());

                // Set expired status for events
                if (session.getStatus() == SessionStatus.PENDING ||
                        session.getStatus() == SessionStatus.CONFIRMED) {
                    deleteEventList.add(session.getPtEventId());
                    deleteEventList.add(session.getUserEventId());
                } else {
                    updateEventList.add(session.getPtEventId());
                    updateEventList.add(session.getUserEventId());
                }

                session.setStatus(SessionStatus.EXPIRED);
                session.setStatusModifiedDate(LocalDateTime.now());

                // Log history
                final SessionHistoryEntity sessionHistoryEntity = new SessionHistoryEntity();
                sessionHistoryEntity.setSessionUuid(session.getUuid());
                sessionHistoryEntity.setUserUuid(session.getUserUuid());
                sessionHistoryEntity.setNewPackageUuid(session.getPackageUuid());
                sessionHistoryEntity.setOldPackageUuid(session.getPackageUuid());
                sessionHistoryEntity.setOldPtUuid(session.getPtUuid());
                sessionHistoryEntity.setNewPtUuid(session.getPtUuid());
                sessionHistoryEntity.setOldStatus(session.getLastStatus());
                sessionHistoryEntity.setNewStatus(session.getStatus());
                sessionHistoryEntity.setAction(SessionAction.AUTO_EXPIRED);

                historyEntities.add(sessionHistoryEntity);
            });

            sessionRepository.save(expiredSessions);

            updateNumberOfBurnedSession(expiredSessions);

            if (!deleteEventList.isEmpty()) {
                eventManagementService.deleteEvents(deleteEventList);
            }

            if (!updateEventList.isEmpty()) {
                eventManagementService.expireEvents(updateEventList);
            }

            sessionHistoryManagementService.save(historyEntities);

            return expiredSessions;
        }

        return Collections.emptyList();
    }

    private void updateNumberOfBurnedSession(final List<SessionEntity> sessionEntities) {
        if (sessionEntities.isEmpty()) {
            return;
        }

        final Map<String, List<SessionEntity>> groupedSessions = sessionEntities.stream()
                .collect(Collectors.groupingBy(SessionEntity::getPackageUuid, Collectors.toList()));

        groupedSessions.entrySet().forEach(entry -> {
            final String packageUuid = entry.getKey();
            final List<SessionEntity> sessionEntityList = entry.getValue();
            final int numberOfExpiredSession = sessionEntityList.size();

            final Optional<SessionPackageEntity> opSessionPackageEntity = packageRepository.getSessionPackageByUuid(packageUuid);
            if (!opSessionPackageEntity.isPresent()) {
                throw new IllegalArgumentException("Can not find a Session Package based on current session");
            }

            final SessionPackageEntity sessionPackageEntity = opSessionPackageEntity.get();

            sessionPackageEntity.setNumOfBurned(sessionPackageEntity.getNumOfBurned() + numberOfExpiredSession);
            packageRepository.save(sessionPackageEntity);

            final Optional<OrderEntity> opOrderEntity = orderRepository.findOneByUuid(sessionPackageEntity.getOrderUuid());
            if (!opOrderEntity.isPresent()) {
                throw new IllegalArgumentException("Can not find a order based on current session");
            }

            final OrderEntity orderEntity = opOrderEntity.get();
            orderEntity.setNumOfBurned(orderEntity.getNumOfBurned() + numberOfExpiredSession);

            orderRepository.save(orderEntity);
        });
    }

    @Override
    public List<SessionEntity> getBookingSession(final String userUuid, final UserType userType) {
        if (userType == UserType.EU) {
            return sessionRepository.getBookingSessionsByUser(userUuid, SessionStatus.getBookingStatus());
        } else {
            return sessionRepository.getBookingSessionsByTrainer(userUuid, SessionStatus.getBookingStatus());
        }
    }

}
