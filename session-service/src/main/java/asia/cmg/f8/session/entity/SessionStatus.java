package asia.cmg.f8.session.entity;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.operations.BurnedSessionOperation;
import asia.cmg.f8.session.operations.CancelledSessionOperation;
import asia.cmg.f8.session.operations.CompletedSessionOperation;
import asia.cmg.f8.session.operations.ConfirmedSessionOperation;
import asia.cmg.f8.session.operations.EUCancelledSessionOperation;
import asia.cmg.f8.session.operations.ExpiredSessionOperation;
import asia.cmg.f8.session.operations.OpenSessionOperation;
import asia.cmg.f8.session.operations.PTCancelledSessionOperation;
import asia.cmg.f8.session.operations.PendingSessionOperation;
import asia.cmg.f8.session.operations.SessionStatusOperations;
import asia.cmg.f8.session.operations.TransferredSessionOperation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created on 11/22/16.
 */
public enum SessionStatus implements SessionStatusOperations {

    OPEN(new OpenSessionOperation(), "Open"),

    PENDING(new PendingSessionOperation(), "Pending"),

    //Per requirement, we should remove when return to client
    CANCELLED(new CancelledSessionOperation(), "Cancelled"),

    //Per requirement, we should remove when return to client
    TRAINER_CANCELLED(new PTCancelledSessionOperation(), "Trainer Cancelled"),

    //Per requirement, we should change it to Cancelled when return to client
    EU_CANCELLED(new EUCancelledSessionOperation(), "End User Cancelled"),

    CONFIRMED(new ConfirmedSessionOperation(), "Confirmed"),

    //Per requirement, we should change it to Burned when return to client
    BURNED(new BurnedSessionOperation(), "Burned"),

    COMPLETED(new CompletedSessionOperation(), "Completed"),

    EXPIRED(new ExpiredSessionOperation(), "Completed"),

    TRANSFERRED(new TransferredSessionOperation(), "Transferred");

    private final String description;

    private final SessionStatusOperations operations;

    SessionStatus(final SessionStatusOperations operations, final String description) {
        this.operations = operations;
        this.description = description;
    }

    @Override
    public SessionStatus book(final SessionEntity sessionEntity, final Account account) {
        return operations.book(sessionEntity, account);
    }

    @Override
    public SessionStatus checkIn(final SessionEntity sessionEntity, final Account account) {
        return operations.checkIn(sessionEntity, account);
    }

    @Override
    public SessionStatus noShow(final SessionEntity sessionEntity, final Account account) {
        return operations.noShow(sessionEntity, account);
    }

    @Override
    public SessionStatus accept(final SessionEntity sessionEntity, final Account account) {
        return operations.accept(sessionEntity, account);
    }

    @Override
    public SessionStatus decline(final SessionEntity sessionEntity, final Account account) {
        return operations.decline(sessionEntity, account);
    }

    @Override
    public SessionStatus cancel(final SessionEntity sessionEntity, final Account account) {
        return operations.cancel(sessionEntity, account);
    }

    @Override
    public SessionStatus defaultStatus() {
        return operations.defaultStatus();
    }

    public String getDescription() {
        return description;
    }

    public static List<SessionStatus> getValidSessionStatus() {
        return Collections.singletonList(
                SessionStatus.COMPLETED);

    }

    public static List<SessionStatus> getCancelledSessionStatus() {
        return Arrays.asList(SessionStatus.CANCELLED, SessionStatus.TRAINER_CANCELLED);
    }

    public static List<SessionStatus> getRevenueSessionStatus() {
        return Arrays.asList(SessionStatus.BURNED, SessionStatus.COMPLETED, SessionStatus.EU_CANCELLED);
    }

    public static List<SessionStatus> getBookableSessionStatus() {
        return Arrays.asList(SessionStatus.OPEN, SessionStatus.CANCELLED,
                SessionStatus.TRAINER_CANCELLED, SessionStatus.TRANSFERRED);
    }

    public static List<SessionStatus> getReviewSessionStatus() {
        return Arrays.asList(SessionStatus.CONFIRMED, SessionStatus.PENDING);
    }

    public static List<SessionStatus> getTrainerRevenueStatus() {
        return Collections.singletonList(
                SessionStatus.COMPLETED);
    }

    public static List<SessionStatus> getTransferableSessionStatus() {
        return Arrays.asList(SessionStatus.OPEN, SessionStatus.CANCELLED,
                SessionStatus.TRAINER_CANCELLED, SessionStatus.TRANSFERRED);
    }

    public static List<SessionStatus> getExpirableSessionStatus() {
        return Arrays.asList(SessionStatus.OPEN, SessionStatus.CANCELLED,
                SessionStatus.TRAINER_CANCELLED, SessionStatus.PENDING, SessionStatus.CONFIRMED, SessionStatus.TRANSFERRED);
    }

    public static List<SessionStatus> getScheduledSessionStatus() {
        return Arrays.asList(SessionStatus.COMPLETED, SessionStatus.BURNED,
                SessionStatus.EU_CANCELLED);
    }

    public static List<SessionStatus> getUserCancelledSessionStatus() {
        return Arrays.asList(SessionStatus.BURNED, SessionStatus.EU_CANCELLED);
    }

    public static SessionStatus mappingClientCancelledSessionStatus(final SessionStatus status) {
        if (status == SessionStatus.EU_CANCELLED) {
            return SessionStatus.CANCELLED;
        }

        return status;
    }

    public static List<SessionStatus> getHistoryShownStatus() {
        return Arrays.asList(SessionStatus.COMPLETED, SessionStatus.CONFIRMED,
                SessionStatus.PENDING, SessionStatus.BURNED, SessionStatus.EU_CANCELLED);
    }

    public static List<SessionStatus> getBookingStatus() {
        return Arrays.asList(SessionStatus.PENDING, SessionStatus.CONFIRMED);
    }
    
    public static List<SessionStatus> getOpenSessionStatus() {
        return Arrays.asList(SessionStatus.OPEN, SessionStatus.PENDING,
                SessionStatus.TRAINER_CANCELLED, SessionStatus.CANCELLED);
    }

}
