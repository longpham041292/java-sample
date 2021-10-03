package asia.cmg.f8.session.wrapper.dto;

import java.math.BigInteger;


/**
 * 
 * @author tung.nguyenthanh
 *
 */
@SuppressWarnings("PMD.TooManyFields")
public class OrderSessionStatusDaily {

    private final String uuid;
    private final int newOpen;
    private final int oldOpen;
    private final int newPending;
    private final int oldPending;
    private final int newConfirmed;
    private final int oldConfirmed;
    private final int eucancelled;
    private final int newPtcancelled;
    private final int oldPtcancelled;
    private final int newCancelled;
    private final int oldCancelled;
    private final int burned;
    private final int completed;
    private final int expired;
    private final int newTransferred;
    private final int oldTransferred;
    private final int numOfSession;
    private final Double commission;
    private final Double ptFee;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public OrderSessionStatusDaily(final String uuid, final BigInteger newOpen,
            final BigInteger oldOpen, final BigInteger newPending, final BigInteger oldPending,
            final BigInteger newConfirmed, final BigInteger oldConfirmed,
            final BigInteger eucancelled, final BigInteger newPtcancelled,
            final BigInteger oldPtcancelled, final BigInteger newCancelled,
            final BigInteger oldCancelled, final BigInteger burned, final BigInteger completed,
            final BigInteger expired, final BigInteger newTransferred,
            final BigInteger oldTransferred, final int numOfSession, final Double commission,
            final Double ptFee) {
        super();
        this.uuid = uuid;
        this.newOpen = newOpen.intValue();
        this.oldOpen = oldOpen.intValue();
        this.newPending = newPending.intValue();
        this.oldPending = oldPending.intValue();
        this.newConfirmed = newConfirmed.intValue();
        this.oldConfirmed = oldConfirmed.intValue();
        this.eucancelled = eucancelled.intValue();
        this.newPtcancelled = newPtcancelled.intValue();
        this.oldPtcancelled = oldPtcancelled.intValue();
        this.newCancelled = newCancelled.intValue();
        this.oldCancelled = oldCancelled.intValue();
        this.burned = burned.intValue();
        this.completed = completed.intValue();
        this.expired = expired.intValue();
        this.newTransferred = newTransferred.intValue();
        this.oldTransferred = oldTransferred.intValue();
        this.numOfSession = numOfSession;
        this.commission = commission;
        this.ptFee = ptFee;
    }

    public String getUuid() {
        return uuid;
    }

    public int getNewOpen() {
        return newOpen;
    }

    public int getOldOpen() {
        return oldOpen;
    }

    public int getNewPending() {
        return newPending;
    }

    public int getOldPending() {
        return oldPending;
    }

    public int getNewConfirmed() {
        return newConfirmed;
    }

    public int getOldConfirmed() {
        return oldConfirmed;
    }

    public int getEucancelled() {
        return eucancelled;
    }

    public int getNewPtcancelled() {
        return newPtcancelled;
    }

    public int getOldPtcancelled() {
        return oldPtcancelled;
    }

    public int getNewCancelled() {
        return newCancelled;
    }

    public int getOldCancelled() {
        return oldCancelled;
    }

    public int getBurned() {
        return burned;
    }

    public int getCompleted() {
        return completed;
    }

    public int getExpired() {
        return expired;
    }

    public int getNewTransferred() {
        return newTransferred;
    }

    public int getOldTransferred() {
        return oldTransferred;
    }

    public int getNumOfSession() {
        return numOfSession;
    }

    public Double getCommission() {
        return commission;
    }

    public Double getPtFee() {
        return ptFee;
    }

}
