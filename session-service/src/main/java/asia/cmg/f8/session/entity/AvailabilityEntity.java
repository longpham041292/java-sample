package asia.cmg.f8.session.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created on 11/21/16.
 */
@Entity
@Table(name = "session_availabilities")
public class AvailabilityEntity extends AbstractEntity {

	@Column(name = "started_time")
    private LocalDateTime startedTime;
	
	@Column(name = "ended_time")
    private LocalDateTime endedTime;
	
	@Column(name = "user_uuid", length = 36)
    private String userId;

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
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
