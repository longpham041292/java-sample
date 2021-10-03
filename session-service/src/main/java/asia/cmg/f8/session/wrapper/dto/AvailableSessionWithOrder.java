package asia.cmg.f8.session.wrapper.dto;

import java.sql.Timestamp;

public class AvailableSessionWithOrder {
    private String sessionUuid;
    private Timestamp startTime;
    private String orderUuid;
    private Timestamp expireDays;
    private int numOfLimitDays;
    private boolean reserved;

    public AvailableSessionWithOrder(final String sessionUuid, final Timestamp startTime,
            final String orderUuid, final Timestamp expireDays, final Integer numOfLimitDays) {
        super();
        this.sessionUuid = sessionUuid;
        this.startTime = startTime;
        this.orderUuid = orderUuid;
        this.expireDays = expireDays;
        this.numOfLimitDays = numOfLimitDays;
    }

    public String getSessionUuid() {
        return sessionUuid;
    }

    public void setSessionUuid(final String sessionUuid) {
        this.sessionUuid = sessionUuid;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(final Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(final String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public Timestamp getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(final Timestamp expireDays) {
        this.expireDays = expireDays;
    }

    public int getNumOfLimitDays() {
        return numOfLimitDays;
    }

    public void setNumOfLimitDays(final int numOfLimitDays) {
        this.numOfLimitDays = numOfLimitDays;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(final boolean reserved) {
        this.reserved = reserved;
    }

}
