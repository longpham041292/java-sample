package asia.cmg.f8.session.dto;

import asia.cmg.f8.common.util.ZoneDateTimeUtils;
import asia.cmg.f8.session.entity.SessionEntity;
import asia.cmg.f8.session.entity.SessionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 1/2/17.
 */
public class Session {
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("user_uuid")
    private String userUuid;
    @JsonProperty("trainer_uuid")
    private String ptUuid;
    @JsonProperty("session_package_uuid")
    private String packageUuid;
    @JsonProperty("status")
    private SessionStatus status;
    @JsonProperty("start_time")
    private Long startTime;
    @JsonProperty("end_time")
    private Long endTime;
    @JsonProperty("event_user_uuid")
    private String userEventId;
    @JsonProperty("event_trainer_uuid")
    private String ptEventId;

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

    public String getPackageUuid() {
        return packageUuid;
    }

    public void setPackageUuid(final String packageUuid) {
        this.packageUuid = packageUuid;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(final SessionStatus status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(final Long endTime) {
        this.endTime = endTime;
    }

    public String getUserEventId() {
        return userEventId;
    }

    public void setUserEventId(final String userEventId) {
        this.userEventId = userEventId;
    }

    public String getPtEventId() {
        return ptEventId;
    }

    public void setPtEventId(final String ptEventId) {
        this.ptEventId = ptEventId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public static Session convertFromEntity(final SessionEntity entity) {
        final Session session = new Session();

        session.setUuid(entity.getUuid());
        session.setUserUuid(entity.getUserUuid());
        session.setPtUuid(entity.getPtUuid());
        session.setPackageUuid(entity.getPackageUuid());
        session.setStatus(entity.getStatus());
        session.setStartTime(entity.getStartTime() != null ? ZoneDateTimeUtils.convertToSecondUTC(entity.getStartTime()) : null);
        session.setEndTime(entity.getEndTime() != null ? ZoneDateTimeUtils.convertToSecondUTC(entity.getEndTime()) : null);
        session.setUserEventId(entity.getUserEventId());
        session.setPtEventId(entity.getPtEventId());

        return session;
    }
}
