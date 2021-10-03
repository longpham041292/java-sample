package asia.cmg.f8.session.entity.credit;

import asia.cmg.f8.common.security.Account;
import asia.cmg.f8.session.credit.operations.SessionStatusOperations;
import asia.cmg.f8.session.credit.operations.BurnedSessionOperation;
import asia.cmg.f8.session.credit.operations.CancelledSessionOperation;
import asia.cmg.f8.session.credit.operations.CompletedSessionOperation;
import asia.cmg.f8.session.credit.operations.ConfirmedSessionOperation;
import asia.cmg.f8.session.credit.operations.EUCancelledSessionOperation;
import asia.cmg.f8.session.credit.operations.BookedSessionOperation;
import asia.cmg.f8.session.credit.operations.PTCancelledSessionOperation;
import asia.cmg.f8.session.credit.operations.RejectedSessionOperation;
import asia.cmg.f8.session.credit.operations.DeductedSessionOperation;
import asia.cmg.f8.session.credit.operations.RefundedSessionOperation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created on 11/22/16.
 */
public enum CreditBookingSessionStatus implements SessionStatusOperations {

    BOOKED(new BookedSessionOperation(), "Booked"),
    
    REJECTED(new RejectedSessionOperation(), "Rejected"),
    
    CONFIRMED(new ConfirmedSessionOperation(), "Confirmed"),

    //Per requirement, we should remove when return to client
    CANCELLED(new CancelledSessionOperation(), "Cancelled"),

    //Per requirement, we should remove when return to client
    TRAINER_CANCELLED(new PTCancelledSessionOperation(), "Trainer Cancelled"),

    //Per requirement, we should change it to Cancelled when return to client
    EU_CANCELLED(new EUCancelledSessionOperation(), "End User Cancelled"),
    
    //Per requirement, we should change it to Burned when return to client
    BURNED(new BurnedSessionOperation(), "Burned"),

    COMPLETED(new CompletedSessionOperation(), "Completed"),
	
	DEDUCTED(new DeductedSessionOperation(), "Deducted"),
	
	REFUNDED(new RefundedSessionOperation(), "Refunded");

    private final String description;

    private final SessionStatusOperations operations;

    CreditBookingSessionStatus(final SessionStatusOperations operations, final String description) {
        this.operations = operations;
        this.description = description;
    }

    @Override
    public CreditBookingSessionStatus book(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.book(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus checkIn(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.checkIn(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus noShow(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.noShow(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus confirm(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.confirm(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus reject(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.reject(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus cancel(final CreditSessionBookingEntity sessionEntity, final Account account) {
        return operations.cancel(sessionEntity, account);
    }

    @Override
    public CreditBookingSessionStatus defaultStatus() {
        return operations.defaultStatus();
    }

    public String getDescription() {
        return description;
    }

    public static List<CreditBookingSessionStatus> getValidSessionStatus() {
        return Collections.singletonList(
                CreditBookingSessionStatus.COMPLETED);

    }

    public static List<CreditBookingSessionStatus> getCancelledSessionStatus() {
        return Arrays.asList(CreditBookingSessionStatus.CANCELLED, CreditBookingSessionStatus.TRAINER_CANCELLED);
    }

    public static List<CreditBookingSessionStatus> getRevenueSessionStatus() {
        return Arrays.asList(CreditBookingSessionStatus.BURNED, CreditBookingSessionStatus.COMPLETED, CreditBookingSessionStatus.EU_CANCELLED);
    }

    public static List<CreditBookingSessionStatus> getReviewSessionStatus() {
        return Arrays.asList(CreditBookingSessionStatus.CONFIRMED, CreditBookingSessionStatus.BOOKED);
    }

    public static List<CreditBookingSessionStatus> getTrainerRevenueStatus() {
        return Collections.singletonList(
                CreditBookingSessionStatus.COMPLETED);
    }

    public static List<CreditBookingSessionStatus> getScheduledSessionStatus() {
        return Arrays.asList(CreditBookingSessionStatus.COMPLETED, CreditBookingSessionStatus.BURNED,
                CreditBookingSessionStatus.EU_CANCELLED);
    }

    public static List<CreditBookingSessionStatus> getUserCancelledSessionStatus() {
        return Arrays.asList(CreditBookingSessionStatus.BURNED, CreditBookingSessionStatus.EU_CANCELLED);
    }

    public static CreditBookingSessionStatus mappingClientCancelledSessionStatus(final CreditBookingSessionStatus status) {
        if (status == CreditBookingSessionStatus.EU_CANCELLED) {
            return CreditBookingSessionStatus.CANCELLED;
        }

        return status;
    }

    public static List<CreditBookingSessionStatus> getHistoryShownStatus() {
        return Arrays.asList(CreditBookingSessionStatus.COMPLETED, CreditBookingSessionStatus.CONFIRMED,
                CreditBookingSessionStatus.BOOKED, CreditBookingSessionStatus.BURNED, CreditBookingSessionStatus.EU_CANCELLED);
    }

    public static List<CreditBookingSessionStatus> getBookingStatus() {
        return Arrays.asList(CreditBookingSessionStatus.BOOKED, CreditBookingSessionStatus.CONFIRMED);
    }
    
    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
