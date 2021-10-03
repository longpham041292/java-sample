package asia.cmg.f8.common.spec.session;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/6/17.
 */
public class TransferSessionPackageInfo {

    @JsonProperty("old_package_uuid")
    private String oldPackageUuid;

    @JsonProperty("new_package_uuid")
    private String newPackageUuid;

    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("user_full_name")
    private String userFullName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("old_trainer_uuid")
    private String oldTrainerUuid;

    @JsonProperty("old_trainer_full_name")
    private String oldTrainerFullName;

    @JsonProperty("old_trainer_email")
    private String oldTrainerEmail;

    @JsonProperty("new_trainer_uuid")
    private String newTrainerUuid;

    @JsonProperty("new_trainer_full_name")
    private String newTrainerFullName;

    @JsonProperty("new_trainer_email")
    private String newTrainerEmail;

    @JsonProperty("order_expired_date")
    private Long orderExpiredDate;

    @JsonProperty("number_of_burned_sessions")
    private Integer numberOfBurnedSessions;

    @JsonProperty("number_of_total_sessions")
    private Integer numberOfTotalSessions;

    public String getOldPackageUuid() {
        return oldPackageUuid;
    }

    public void setOldPackageUuid(final String oldPackageUuid) {
        this.oldPackageUuid = oldPackageUuid;
    }

    public String getNewPackageUuid() {
        return newPackageUuid;
    }

    public void setNewPackageUuid(final String newPackageUuid) {
        this.newPackageUuid = newPackageUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(final String userFullName) {
        this.userFullName = userFullName;
    }

    public String getOldTrainerUuid() {
        return oldTrainerUuid;
    }

    public void setOldTrainerUuid(final String oldTrainerUuid) {
        this.oldTrainerUuid = oldTrainerUuid;
    }

    public String getOldTrainerFullName() {
        return oldTrainerFullName;
    }

    public void setOldTrainerFullName(final String oldTrainerFullName) {
        this.oldTrainerFullName = oldTrainerFullName;
    }

    public String getNewTrainerUuid() {
        return newTrainerUuid;
    }

    public void setNewTrainerUuid(final String newTrainerUuid) {
        this.newTrainerUuid = newTrainerUuid;
    }

    public String getNewTrainerFullName() {
        return newTrainerFullName;
    }

    public void setNewTrainerFullName(final String newTrainerFullName) {
        this.newTrainerFullName = newTrainerFullName;
    }

    public Long getOrderExpiredDate() {
        return orderExpiredDate;
    }

    public void setOrderExpiredDate(final Long orderExpiredDate) {
        this.orderExpiredDate = orderExpiredDate;
    }

    public Integer getNumberOfBurnedSessions() {
        return numberOfBurnedSessions;
    }

    public void setNumberOfBurnedSessions(final Integer numberOfBurnedSessions) {
        this.numberOfBurnedSessions = numberOfBurnedSessions;
    }

    public Integer getNumberOfTotalSessions() {
        return numberOfTotalSessions;
    }

    public void setNumberOfTotalSessions(final Integer numberOfTotalSessions) {
        this.numberOfTotalSessions = numberOfTotalSessions;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    public String getOldTrainerEmail() {
        return oldTrainerEmail;
    }

    public void setOldTrainerEmail(final String oldTrainerEmail) {
        this.oldTrainerEmail = oldTrainerEmail;
    }

    public String getNewTrainerEmail() {
        return newTrainerEmail;
    }

    public void setNewTrainerEmail(final String newTrainerEmail) {
        this.newTrainerEmail = newTrainerEmail;
    }
}
