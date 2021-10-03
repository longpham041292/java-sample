package asia.cmg.f8.session.dto;

import asia.cmg.f8.session.entity.SessionPackageEntity;
import asia.cmg.f8.session.entity.SessionPackageStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created on 12/27/16.
 */
public class SessionPackage {

    @JsonProperty("session_package_uuid")
    private String uuid;

    @JsonProperty("order_uuid")
    private String orderUuid;

    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("trainer_uuid")
    private String ptUuid;

    @JsonProperty("status")
    private SessionPackageStatus status;

    @JsonProperty("number_of_burned")
    private Integer numberOfBurned;

    @JsonProperty("number_of_session")
    private Integer numberOfSession;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(final String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(final String userUuid) {
        this.userUuid = userUuid;
    }

    public String getPtUuid() {
        return ptUuid;
    }

    public void setPtUuid(final String ptUuid) {
        this.ptUuid = ptUuid;
    }

    public SessionPackageStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionPackageStatus status) {
        this.status = status;
    }

    public Integer getNumberOfBurned() {
        return numberOfBurned;
    }

    public void setNumberOfBurned(final Integer numberOfBurned) {
        this.numberOfBurned = numberOfBurned;
    }

    public Integer getNumberOfSession() {
        return numberOfSession;
    }

    public void setNumberOfSession(final Integer numberOfSession) {
        this.numberOfSession = numberOfSession;
    }

    public static SessionPackage convertFromEntity(final SessionPackageEntity entity) {
        if (Objects.isNull(entity)) {
            return new SessionPackage();
        }

        final SessionPackage sessionPackage = new SessionPackage();
        sessionPackage.setUuid(entity.getUuid());
        sessionPackage.setOrderUuid(entity.getOrderUuid());
        sessionPackage.setUserUuid(entity.getUserUuid());
        sessionPackage.setPtUuid(entity.getPtUuid());
        sessionPackage.setStatus(entity.getStatus());
        sessionPackage.setNumberOfBurned(entity.getNumOfBurned());
        sessionPackage.setNumberOfSession(entity.getNumOfSessions());

        return sessionPackage;
    }
}
