package asia.cmg.f8.session.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "session_schedule_events", indexes = { @Index(name = "IDX_OWNER_UUID",
        columnList = "owner_uuid") })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ScheduleEvent extends AbstractEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "owner_uuid", length = 36)
    private String ownerUuid;

    @Column(name = "title", length = 128)
    private String title;

    @Column(name = "available_to_train", nullable = false, columnDefinition = "boolean default false")
    private boolean availableToTrain;

    @Column(name = "started_time")
    private LocalDateTime startedTime;

    @Column(name = "ended_time")
    private LocalDateTime endedTime;

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(final String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isAvailableToTrain() {
        return availableToTrain;
    }

    public void setAvailableToTrain(final boolean availableToTrain) {
        this.availableToTrain = availableToTrain;
    }

    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(final LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

    public LocalDateTime getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(final LocalDateTime endedTime) {
        this.endedTime = endedTime;
    }
}
