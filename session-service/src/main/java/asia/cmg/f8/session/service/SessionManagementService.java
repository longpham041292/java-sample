package asia.cmg.f8.session.service;

import java.util.List;

import asia.cmg.f8.common.spec.user.UserType;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import asia.cmg.f8.session.wrapper.dto.AvailableSessionWithOrder;

/**
 * Created on 12/21/16.
 */
public interface SessionManagementService {

    /**
     * Setup new session for given {@link SessionPackageEntity}.
     *
     * @param packageEntity the package entity
     * @return list of created {@link SessionEntity}.
     */
    List<SessionEntity> setupSession(SessionPackageEntity packageEntity);

    /**
     * Transfer sessions from given package to new package.
     *
     * @param oldPackage the old package.
     * @param newPackage the new package.
     * @return list of transferred sessions
     */
    List<SessionEntity> transferSession(SessionPackageEntity oldPackage, SessionPackageEntity newPackage);

    /**
     * @return list of {@link SessionStatus} that are eligible for transferring.
     */
    List<SessionStatus> getValidStatusForTransfer();

    /**
     * Update session
     *
     * @param sessionEntity the session
     * @return updated session
     */
    SessionEntity save(SessionEntity sessionEntity);

    /**
     * Get all available session with OPEN or CANCELLED {@link SessionStatus}
     * between EndUser and Trainer
     *
     * @param userId    the user id
     * @param trainerId the trainer id
     * @return a list of available sessions {@link SessionEntity} that can be booked.
     */
    List<AvailableSessionWithOrder> getAvailableSessions(final String userId, final String trainerId);
    
    /**
     * Get all available session with OPEN or CANCELLED {@link SessionStatus}
     * between EndUser and Trainer
     *
     * @param userId    the user id
     * @param packageId the packageId id
     * @return a list of available sessions {@link SessionEntity} that can be booked.
     */
     List<AvailableSessionWithOrder> getAvailableSessionsByPackageId(final String userId, final String packageId);

    /**
     * Set expired session status for all expired order. This is might called by Schedule job.
     *
     * @return List of expired session.
     */
    List<SessionEntity> autoExpiredSession();

    List<SessionEntity> getBookingSession(String userUuid, UserType userType);
}
