package asia.cmg.f8.session.wrapper.dto;

import java.math.BigInteger;

@SuppressWarnings({"squid:S2384", "PMD.TooManyFields"})
public class OrderSession {

    private String uuid;
    private String avatar;
    private String fullName;
    private Double commission;
    private Double price;
    private Integer sessionNumber;
    private Integer open;
    private Integer confirmed;
    private Integer euCancelled;
    private Integer burned;
    private Integer completed;
    private Integer expired;
    private Integer transferred;
    private Integer ptFeeSessions;
    private Integer revenueSession;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public OrderSession(final String uuid, final String avatar, final String fullName, final Double commission,
            final Double price, final Integer sessionNumber, final BigInteger open, final BigInteger confirmed,
            final BigInteger euCancelled, final BigInteger burned, final BigInteger completed, final BigInteger expired,
            final BigInteger transferred, final BigInteger ptFeeSessions, final BigInteger revenueSession) {
        super();
        this.uuid = uuid;
        this.avatar = avatar;
        this.fullName = fullName;
        this.commission = commission;
        this.price = price;
        this.sessionNumber = sessionNumber;
        this.open = open.intValue();
        this.confirmed = confirmed.intValue();
        this.euCancelled = euCancelled.intValue();
        this.burned = burned.intValue();
        this.completed = completed.intValue();
        this.expired = expired.intValue();
        this.transferred = transferred.intValue();
        this.ptFeeSessions = ptFeeSessions.intValue();
        this.revenueSession = revenueSession.intValue();
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }
    public Double getCommission() {
        return commission;
    }
    public void setCommission(final Double commission) {
        this.commission = commission;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(final Double price) {
        this.price = price;
    }
    public Integer getSessionNumber() {
        return sessionNumber;
    }
    public void setSessionNumber(final Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }
    public Integer getOpen() {
        return open;
    }
    public void setOpen(final Integer open) {
        this.open = open;
    }
    public Integer getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(final Integer confirmed) {
        this.confirmed = confirmed;
    }
    public Integer getEuCancelled() {
        return euCancelled;
    }
    public void setEuCancelled(final Integer euCancelled) {
        this.euCancelled = euCancelled;
    }
    public Integer getBurned() {
        return burned;
    }
    public void setBurned(final Integer burned) {
        this.burned = burned;
    }
    public Integer getCompleted() {
        return completed;
    }
    public void setCompleted(final Integer completed) {
        this.completed = completed;
    }
    public Integer getExpired() {
        return expired;
    }
    public void setExpired(final Integer expired) {
        this.expired = expired;
    }
    public Integer getTransferred() {
        return transferred;
    }
    public void setTransferred(final Integer transferred) {
        this.transferred = transferred;
    }
    public Integer getPtFeeSessions() {
        return ptFeeSessions;
    }
    public void setPtFeeSessions(final Integer ptFeeSessions) {
        this.ptFeeSessions = ptFeeSessions;
    }
    public Integer getRevenueSession() {
        return revenueSession;
    }
    public void setRevenueSession(final Integer revenueSession) {
        this.revenueSession = revenueSession;
    }
    
}
