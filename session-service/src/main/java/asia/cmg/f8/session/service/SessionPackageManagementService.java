package asia.cmg.f8.session.service;

import asia.cmg.f8.common.spec.session.CurrentTransferPackageInfo;
import asia.cmg.f8.session.entity.OrderEntity;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.entity.SessionPackageStatus;

import java.util.List;
import java.util.Optional;

/**
 * Created on 12/21/16.
 */
public interface SessionPackageManagementService {

    /**
     * Setup new {@link SessionPackageEntity} for given order.
     * It will pre-create {@link SessionEntity}s.
     * The number of created sessions are based on the value of {@link OrderEntity#numOfSessions}.
     *
     * @param order the order
     * @return new created session package.
     */
    SessionPackageEntity setupPackage(OrderEntity order);

    /**
     * Setup new {@link SessionPackageEntity} from given {@link SessionPackageEntity}.
     * It will not create new {@link SessionEntity} but update value of {@link SessionEntity#packageUuid} property to new created {@link SessionPackageEntity}
     * Sessions which has status are {@link SessionStatus#OPEN}, {@link SessionStatus#CANCELLED} and {@link SessionStatus#PENDING} are eligible for transferring.
     *
     * @param sessionPackage the session package
     * @return the new created session package.
     */
    Optional<SessionPackageEntity> transferPackage(SessionPackageEntity sessionPackage, String newTrainer);

    List<Object[]> getSessionPackageEntitiesbyUser(String userUuid,long expiredDate, SessionPackageStatus differentStatus);

    /**
     * Get a session package by session uuid
     *
     * @param sessionUuid Session's uuid
     * @return {@link Optional<SessionPackageEntity>}
     */
    Optional<SessionPackageEntity> getSessionPackageBySessionUUID(String sessionUuid);

	Optional<SessionPackageEntity> getSessionPackageBySessionPackageUuid(final String sessionUuid);

    SessionPackageEntity saveSessionPackage(SessionPackageEntity sessionPackageEntity);

    Optional<SessionPackageEntity> getTransferableSessionPackageByUuid(String packageUuid);

    Optional<SessionPackageEntity> getSessionPackageByUuid(String packageUuid);

    Optional<CurrentTransferPackageInfo> getTransferSessionPackageInfo(String oldPackageUuid, String newPackageUuid);
}
