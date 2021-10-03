package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PTSessionStatRevenue {

    private String uuid;
    @JsonProperty("full_name")
    private String fullName;
    private String avatar;


    private Integer expired;
    private String revenue;

    @JsonProperty("service_fee")
    private String serviceFee;

    private Integer open;
    private Integer confirmed;
    private Integer cancelled;
    private Integer noShow;
    private Integer used;
    private Integer transferred;

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

    public Integer getExpired() {
        return expired;
    }

    public void setExpired(final Integer expired) {
        this.expired = expired;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(final String revenue) {
        this.revenue = revenue;
    }

    public String getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(final String serviceFee) {
        this.serviceFee = serviceFee;
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

    public Integer getOpen() {
        return open;
    }

    public void setOpen(final Integer open) {
        this.open = open;
    }

    public Integer getNoShow() {
        return noShow;
    }

    public void setNoShow(final Integer noShow) {
        this.noShow = noShow;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(final Integer used) {
        this.used = used;
    }

    public Integer getTransferred() {
        return transferred;
    }

    public void setTransferred(final Integer transferred) {
        this.transferred = transferred;
    }

}
