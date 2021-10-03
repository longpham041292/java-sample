package asia.cmg.f8.session.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created on 12/21/16.
 */
@Entity
@Table(name = "session_session_packages", uniqueConstraints = {
        @UniqueConstraint(name = "package_uuid", columnNames = {"uuid", "order_uuid"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SessionPackageEntity extends AbstractEntity {

	@Column(name = "order_uuid", length = 36, nullable = false)
    private String orderUuid;
	
	@Column(name = "user_uuid", length = 36, nullable = false)
    private String userUuid;
	
	@Column(name = "pt_uuid", length = 36, nullable = false)
    private String ptUuid;
	
	@Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SessionPackageStatus status;
	
	@Column(name = "last_status")
	@Enumerated(EnumType.STRING)
	private SessionPackageStatus lastStatus;

	@Column(name = "num_of_burned")
	private int numOfBurned;
	
	@Column(name = "num_of_sessions")
    private int numOfSessions;
    
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

	public SessionPackageStatus getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(SessionPackageStatus lastStatus) {
		this.lastStatus = lastStatus;
	}

    public int getNumOfBurned() {
        return numOfBurned;
    }

    public void setNumOfBurned(final int numOfBurned) {
        this.numOfBurned = numOfBurned;
    }

    public int getNumOfSessions() {
        return numOfSessions;
    }

    public void setNumOfSessions(final int numOfSessions) {
        this.numOfSessions = numOfSessions;
    }
}
