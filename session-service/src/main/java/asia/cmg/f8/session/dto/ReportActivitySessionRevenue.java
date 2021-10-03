package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


@SuppressWarnings("PMD.ExcessiveParameterList")
public class ReportActivitySessionRevenue {

    private String sessionDate;
    private Integer open;
    private Integer confirmed;
    private Integer cancelled;
    private Integer noShown;
    private Integer used;
    private Integer expired;
    private Integer transferred;
    private Double commission;

    @JsonProperty("service_fee")
    private Double serviceFee;

    @JsonProperty("display_commission")
    private String displayCommission;

    @JsonProperty("display_service_fee")
    private String displayServiceFee;

    public ReportActivitySessionRevenue(final String sessionDate, final Integer open,
            final Integer confirmed, final Integer cancelled, final Integer noShown,
            final Integer used, final Integer expired, final Integer transferred,
            final String displayCommission,
            final String displayServiceFee) {
        super();
        this.sessionDate = sessionDate;
        this.open = open;
        this.confirmed = confirmed;
        this.cancelled = cancelled;
        this.noShown = noShown;
        this.used = used;
        this.expired = expired;
        this.transferred = transferred;
        this.displayCommission = displayCommission;
        this.displayServiceFee = displayServiceFee;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(final String sessionDate) {
        this.sessionDate = sessionDate;
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

    public String getDisplayCommission() {
        return displayCommission;
    }

    public void setDisplayCommission(final String displayCommission) {
        this.displayCommission = displayCommission;
    }

    public String getDisplayServiceFee() {
        return displayServiceFee;
    }

    public void setDisplayServiceFee(final String displayServiceFee) {
        this.displayServiceFee = displayServiceFee;
    }

    public Integer getTransferred() {
        return transferred;
    }

    public void setTransferred(final Integer transferred) {
        this.transferred = transferred;
    }

}
