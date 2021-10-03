package asia.cmg.f8.session.wrapper.dto;

import java.time.LocalDate;


@SuppressWarnings("squid:S2384")
public class ActivitySessionRevenue {

    private LocalDate date;
    private Integer open;
    private Integer confirmed;
    private Integer cancelled;
    private Integer noShown;
    private Integer used;
    private Integer expired;
    private Integer transferred;
    private Double commission;
    private Double serviceFee;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public ActivitySessionRevenue(final LocalDate date, final Long open,
            final Long confirmed, final Long cancelled, final Long noShown, final Long used,
            final Long expired, final Long transferred,
            final Double commission, final Double serviceFee) {
        super();
        this.date = date;
        this.open = open.intValue();
        this.confirmed = confirmed.intValue();
        this.cancelled = cancelled.intValue();
        this.noShown = noShown.intValue();
        this.used = used.intValue();
        this.expired = expired.intValue();
        this.transferred = transferred.intValue();
        this.commission = commission;
        this.serviceFee = serviceFee;
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

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(final Integer cancelled) {
        this.cancelled = cancelled;
    }

    public Integer getNoShown() {
        return noShown;
    }

    public void setNoShown(final Integer noShown) {
        this.noShown = noShown;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(final Integer used) {
        this.used = used;
    }

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(final Integer expired) {
        this.expired = expired;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(final Double commission) {
        this.commission = commission;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(final Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Integer getTransferred() {
        return transferred;
    }

    public void setTransferred(final Integer transferred) {
        this.transferred = transferred;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

}
