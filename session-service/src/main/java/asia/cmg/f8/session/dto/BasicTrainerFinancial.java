package asia.cmg.f8.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author tung.nguyenthanh
 */
@SuppressWarnings("PMD.TooManyFields")
public class BasicTrainerFinancial {

    private String uuid;

    @JsonProperty("full_name")
    private String fullname;

    @JsonProperty("user_name")
    private String userName;

    private String avatar;

    private String city;

    private String country;

    @JsonProperty("doc_status")
    private String docStatus;

    private Boolean activated;

    private String revenue;

    private String commission;

    @JsonProperty("display_join_date")
    private String displayJoinDate;

    private String email;

    @JsonProperty("cert_status")
    private String certStatus;

    private String level;

    @JsonProperty("session_purchased")
    private Integer sessionPurchased;

    @JsonProperty("cancelled_users")
    private Integer cancelledUsers;

    @JsonProperty("noshow_users")
    private Integer noshowUsers;

    @JsonProperty("new_users")
    private Integer newUsers;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(final String fullname) {
        this.fullname = fullname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(final String docStatus) {
        this.docStatus = docStatus;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(final Boolean activated) {
        this.activated = activated;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(final String revenue) {
        this.revenue = revenue;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(final String commission) {
        this.commission = commission;
    }

    public String getDisplayJoinDate() {
        return displayJoinDate;
    }

    public void setDisplayJoinDate(final String displayJoinDate) {
        this.displayJoinDate = displayJoinDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(final String certStatus) {
        this.certStatus = certStatus;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    public Integer getSessionPurchased() {
        return sessionPurchased;
    }

    public void setSessionPurchased(final Integer sessionPurchased) {
        this.sessionPurchased = sessionPurchased;
    }

    public Integer getCancelledUsers() {
        return cancelledUsers;
    }

    public void setCancelledUsers(final Integer cancelledUsers) {
        this.cancelledUsers = cancelledUsers;
    }

    public Integer getNoshowUsers() {
        return noshowUsers;
    }

    public void setNoshowUsers(final Integer noshowUsers) {
        this.noshowUsers = noshowUsers;
    }

    public Integer getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(final Integer newUsers) {
        this.newUsers = newUsers;
    }

}
